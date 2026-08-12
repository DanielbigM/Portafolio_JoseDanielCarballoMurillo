package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Rol;
import TechShop.JoseDaniel.domain.Usuario;
import TechShop.JoseDaniel.repository.RolRepository;
import TechShop.JoseDaniel.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            CloudinaryService cloudinaryService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsernameOrCorreo(String username, String correo) {
        return usuarioRepository.findByUsernameOrCorreo(username, correo);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsernameAndPassword(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsernameCorreo(String username, String correo) {
        return usuarioRepository.findByUsernameOrCorreo(username, correo);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioPorUsernameCorreo(String username, String correo) {
        return usuarioRepository.existsByUsernameOrCorreo(username, correo);
    }

    @Transactional
    public void save(Usuario usuario, MultipartFile imagenFile, boolean encriptaClave) {
        boolean esNuevo = (usuario.getIdUsuario() == null);

        if (esNuevo) {
            Optional<Usuario> duplicado = usuarioRepository.findByUsernameOrCorreo(usuario.getUsername(), usuario.getCorreo());
            if (duplicado.isPresent()) {
                throw new DataIntegrityViolationException("El nombre de usuario o correo ya existe.");
            }
        }

        if (encriptaClave) {
            if (esNuevo) {
                if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                    throw new IllegalArgumentException("La contraseña no puede estar vacía.");
                }
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                } else {
                    Usuario existente = usuarioRepository.findById(usuario.getIdUsuario().intValue())
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
                    usuario.setPassword(existente.getPassword());
                }
            }
        }

        // Subida de imagen a Cloudinary
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String ruta = cloudinaryService.subirImagen(imagenFile, "usuario");
                usuario.setRutaImagen(ruta);
            } catch (java.io.IOException e) {
                throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage(), e);
            }
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }

    @Transactional
    public Usuario asignarRolPorUsername(String username, String rolStr) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Rol rol = rolRepository.findByNombre(rolStr)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolStr));

        usuario.getRoles().add(rol);
        return usuarioRepository.save(usuario);
    }

    // Método para obtener la lista de todos los nombres de roles en el sistema
    @Transactional(readOnly = true)
    public List<String> getRolesNombres() {
        return List.of("ROLE_ADMIN", "ROLE_VENDEDOR", "ROLE_USER");
    }

    // Método para eliminar/revocar un rol de un usuario
    @Transactional
    public Usuario eliminarRol(String username, Integer idRol) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
        Usuario usuario = usuarioOpt.get();

        usuario.getRoles().removeIf(rol -> rol.getIdRol().equals(idRol));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void asignarRol(Long idUsuario, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario.intValue()).orElse(null);
        if (usuario != null) {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombreRol);
            nuevoRol.setUsuario(usuario);
            rolRepository.save(nuevoRol);
        }
    }

    @Transactional
    public void eliminarRol(Long idUsuario, Long idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario.intValue()).orElse(null);
        if (usuario != null) {
            usuario.getRoles().removeIf(rol -> rol.getIdRol().equals(idRol));
            usuarioRepository.save(usuario);
        }
    }
}
