// ============================================================
// rutinas.js - TechShop
// Funciones JS reutilizables para el sitio
// ============================================================

// 1. Preview de imagen al seleccionar un archivo

function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; // 512 KB máximo

        if (imagen.size <= maximo) {
            var lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(input.files[0]);
        } else {
            alert("La imagen seleccionada es muy grande... no debe superar los 512 Kb!");
            input.value = ""; // limpia el input
        }
    }
}

// 2. Pasar datos al modal de confirmación de eliminar
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');

    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('modalId').value = button.getAttribute('data-bs-id');
            document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
        });
    }
});

// 3. Auto-ocultar los toasts (notificaciones) después de 4 segundos
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);

/* Función para agregar productos al carrito mediante AJAX */
function addCart(formulario) {
    // 1. Extraer la URL de la acción del formulario
    var url = formulario.action;

    // 2. Obtener el token y el header CSRF desde las etiquetas <meta> del HTML
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // 3. Serializar los datos del formulario (ej. idProducto)
    var formData = $(formulario).serialize();

    // 4. Petición AJAX POST
    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        beforeSend: function(xhr) {
            // Incluir el token CSRF en el encabezado de la petición
            if (csrfHeader && csrfToken) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        },
        success: function(response) {
            // Actualizar el contenedor del carrito en la interfaz con el fragmento devuelto
            $("#resultsBlock").html(response);
        },
        error: function(xhr, status, error) {
            console.error("Error al agregar al carrito:", error);
            alert("Ocurrió un error al intentar agregar el producto al carrito.");
        }
    });
}

