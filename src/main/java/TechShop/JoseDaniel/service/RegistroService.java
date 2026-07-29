/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Usuario;
import jakarta.mail.MessagingException;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegistroService {

    private final CorreoService correoService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    @Value("${servidor.http}")
    private String servidorHttp;

    public RegistroService(CorreoService correoService, UsuarioService usuarioService, MessageSource messageSource) {
        this.correoService = correoService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    // 1. Solicitud de nuevo usuario
    public Model crearUsuario(Model model, Usuario usuario) throws MessagingException {
        String mensaje;
        if (!usuarioService.existeUsuarioPorUsernameCorreo(usuario.getUsername(), usuario.getCorreo())) {
            String clave = demeClave();
            usuario.setPassword(clave);
            usuario.setActivo(false);
            usuarioService.save(usuario, null, true);
            enviaCorreoActivar(usuario, clave);
            mensaje = String.format(
                messageSource.getMessage("registro.mensaje.activacion.exito", null, "Correo enviado a %s para activar la cuenta.", Locale.getDefault()),
                usuario.getCorreo()
            );
        } else {
            mensaje = messageSource.getMessage("registro.mensaje.usuario.o.correo.existe", null, "El usuario o correo ya existe.", Locale.getDefault());
        }
        model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, "Activar Cuenta", Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    // 2. Carga de datos para activar (al dar clic en el link del correo)
    public Model activar(Model model, String username, String clave) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorUsernameAndPassword(username, clave);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
        } else {
            model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, "Error de Activación", Locale.getDefault()));
            model.addAttribute("mensaje", messageSource.getMessage("registro.activar.error", null, "No se pudo activar la cuenta o el enlace expiró.", Locale.getDefault()));
        }
        return model;
    }

    // 3. Guardar el usuario ya activado con su contraseña final y foto
    public void activar(Usuario usuario, MultipartFile imagenFile) {
        usuario.setActivo(true);
        usuarioService.save(usuario, imagenFile, true);
    }

    // 4. Proceso para "Recordar Contraseña"
    public Model recordarUsuario(Model model, Usuario usuario) throws MessagingException {
        String mensaje;
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorUsernameOrCorreo(usuario.getUsername(), usuario.getCorreo());
        if (usuarioOpt.isPresent()) {
            Usuario usuarioExistente = usuarioOpt.get();
            String clave = demeClave();
            usuarioExistente.setPassword(clave);
            usuarioService.save(usuarioExistente, null, false);
            enviaCorreoRecordar(usuarioExistente, clave);
            mensaje = messageSource.getMessage("registro.mensaje.recordar.exito", null, "Se han enviado las instrucciones a su correo.", Locale.getDefault());
        } else {
            mensaje = messageSource.getMessage("registro.mensaje.usuario.no.existe", null, "El usuario o correo no existe.", Locale.getDefault());
        }
        model.addAttribute("titulo", messageSource.getMessage("registro.recordar.us", null, "Recordar Usuario", Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }

    private void enviaCorreoActivar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = String.format(
            "Hola %s %s,<br/><br/>Para activar tu cuenta en TechShop haz clic en el siguiente enlace:<br/><br/>"
            + "<a href='%s/registro/activacion/%s/%s'>Activar Cuenta</a>",
            usuario.getNombre(), usuario.getApellidos(), servidorHttp, usuario.getUsername(), clave
        );
        String asunto = messageSource.getMessage("registro.mensaje.activacion", null, "Activación de cuenta TechShop", Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje);
    }

    private void enviaCorreoRecordar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = String.format(
            "Hola %s %s,<br/><br/>Has solicitado restablecer tu contraseña. Haz clic en el enlace para actualizar tus datos:<br/><br/>"
            + "<a href='%s/registro/activacion/%s/%s'>Restablecer Contraseña</a>",
            usuario.getNombre(), usuario.getApellidos(), servidorHttp, usuario.getUsername(), clave
        );
        String asunto = messageSource.getMessage("registro.mensaje.recordar", null, "Restablecer contraseña TechShop", Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje);
    }

    private String demeClave() {
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            clave.append((char) (Math.random() * 26 + 'a'));
        }
        return clave.toString();
    }
}