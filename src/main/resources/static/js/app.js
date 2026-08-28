document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formularioMatricula');
    if (!form) return;

    // Función auxiliar para buscar elementos por múltiples IDs posibles
    function getEl(...ids) {
        for (let id of ids) {
            const el = document.getElementById(id);
            if (el) return el;
        }
        return null;
    }

    function getVal(...ids) {
        const el = getEl(...ids);
        return el ? el.value.trim() : '';
    }

    // 1. Obtención flexible de referencias del DOM
    const cursoActual = getEl('cursoActual');
    const alumnaRut = getEl('alumnaRut', 'aluRut');
    const alumnaFechaNac = getEl('alumnaFechaNac', 'aluFechaNac');
    const alumnaNombres = getEl('alumnaNombres', 'aluNombres');
    const alumnaApellidoP = getEl('alumnaApellidoP', 'aluPaterno');
    const alumnaApellidoM = getEl('alumnaApellidoM', 'aluMaterno');
    const alumnaDireccion = getEl('alumnaDireccion', 'aluDireccion');
    const alumnaComuna = getEl('alumnaComuna', 'aluComuna');
    const alumnaViveCon = getEl('alumnaViveCon', 'aluViveCon');

    // Apoderado Titular (Apoderado 1)
    const checkMadreApo1 = getEl('checkMadreEsApoderado');
    const checkPadreApo1 = getEl('checkPadreEsApoderado');
    const apoRut = getEl('apoRut');
    const apoNombres = getEl('apoNombres');
    const apoApellidos = getEl('apoApellidos');
    const apoParentesco = getEl('apoParentesco');
    const apoTelefono = getEl('apoTelefono');
    const apoCorreo = getEl('apoCorreo');
    const apoDireccion = getEl('apoDireccion');
    const apoComuna = getEl('apoComuna');

    // Apoderado Suplente (Apoderado 2)
    const checkMadreApo2 = getEl('checkMadreEsApoderado2');
    const checkPadreApo2 = getEl('checkPadreEsApoderado2');
    const apoRut2 = getEl('apoRut2');
    const apoNombres2 = getEl('apoNombres2');
    const apoApellidos2 = getEl('apoApellidos2');
    const apoParentesco2 = getEl('apoParentesco2');
    const apoTelefono2 = getEl('apoTelefono2');
    const apoCorreo2 = getEl('apoCorreo2');
    const apoDireccion2 = getEl('apoDireccion2');
    const apoComuna2 = getEl('apoComuna2');

    // Madre
    const madreRut = getEl('madreRut');
    const madreNombres = getEl('madreNombres');
    const madreApellidos = getEl('madreApellidos');
    const madreTelefono = getEl('madreTelefono');
    const madreDireccion = getEl('madreDireccion');
    const madreComuna = getEl('madreComuna');

    // Padre
    const padreRut = getEl('padreRut');
    const padreNombres = getEl('padreNombres');
    const padreApellidos = getEl('padreApellidos');
    const padreTelefono = getEl('padreTelefono');
    const padreDireccion = getEl('padreDireccion');
    const padreComuna = getEl('padreComuna');

    // Retiro y Salud
    const retiraRut = getEl('retiraRut');
    const retiraNombreCompleto = getEl('retiraNombreCompleto');
    const retiraParentesco = getEl('retiraParentesco');
    const retiraTelefono = getEl('retiraTelefono');

    const esAlergica = getEl('esAlergica', 'medAlergica');
    const detalleAlergias = getEl('detalleAlergias', 'medAlergiasDetalle');
    const tomaMedicamentos = getEl('tomaMedicamentos', 'medMedicamentos');
    const detalleMedicamentos = getEl('detalleMedicamentos', 'medMedicamentosDetalle');
    const condicionMedicaAdicional = getEl('condicionMedicaAdicional', 'medCondicion');

    const btnEnviar = getEl('btnEnviar');

    // 2. Control de habilitación de campos médicos
    if (esAlergica && detalleAlergias) {
        esAlergica.addEventListener('change', (e) => {
            const esTrue = e.target.type === 'checkbox' ? e.target.checked : e.target.value === 'true';
            detalleAlergias.disabled = !esTrue;
            if (detalleAlergias.disabled) detalleAlergias.value = '';
        });
    }

    if (tomaMedicamentos && detalleMedicamentos) {
        tomaMedicamentos.addEventListener('change', (e) => {
            const esTrue = e.target.type === 'checkbox' ? e.target.checked : e.target.value === 'true';
            detalleMedicamentos.disabled = !esTrue;
            if (detalleMedicamentos.disabled) detalleMedicamentos.value = '';
        });
    }

    // 3. Sincronización de Checkboxes Madre/Padre -> Apoderados
    function copiarValores(srcRut, srcNom, srcApe, srcTel, srcDir, srcCom, destRut, destNom, destApe, destTel, destDir, destCom) {
        if (srcRut && destRut && srcRut.value) destRut.value = srcRut.value;
        if (srcNom && destNom && srcNom.value) destNom.value = srcNom.value;
        if (srcApe && destApe && srcApe.value) destApe.value = srcApe.value;
        if (srcTel && destTel && srcTel.value) destTel.value = srcTel.value;
        if (srcDir && destDir && srcDir.value) destDir.value = srcDir.value;
        if (srcCom && destCom && srcCom.value) destCom.value = srcCom.value;
    }

    function sincronizarFormulario() {
        if (checkMadreApo1 && checkMadreApo1.checked) {
            if (checkPadreApo1) checkPadreApo1.checked = false;
            if (apoParentesco) apoParentesco.value = "MAMA";
            if (checkMadreApo2) checkMadreApo2.checked = false;
            copiarValores(apoRut, apoNombres, apoApellidos, apoTelefono, apoDireccion, apoComuna, madreRut, madreNombres, madreApellidos, madreTelefono, madreDireccion, madreComuna);
        }

        if (checkPadreApo1 && checkPadreApo1.checked) {
            if (checkMadreApo1) checkMadreApo1.checked = false;
            if (apoParentesco) apoParentesco.value = "PAPA";
            if (checkPadreApo2) checkPadreApo2.checked = false;
            copiarValores(apoRut, apoNombres, apoApellidos, apoTelefono, apoDireccion, apoComuna, padreRut, padreNombres, padreApellidos, padreTelefono, padreDireccion, padreComuna);
        }

        if (checkMadreApo2 && checkMadreApo2.checked) {
            if (checkPadreApo2) checkPadreApo2.checked = false;
            if (apoParentesco2) apoParentesco2.value = "MAMA";
            if (checkMadreApo1) checkMadreApo1.checked = false;
            copiarValores(apoRut2, apoNombres2, apoApellidos2, apoTelefono2, apoDireccion2, apoComuna2, madreRut, madreNombres, madreApellidos, madreTelefono, madreDireccion, madreComuna);
        }

        if (checkPadreApo2 && checkPadreApo2.checked) {
            if (checkMadreApo2) checkMadreApo2.checked = false;
            if (apoParentesco2) apoParentesco2.value = "PAPA";
            if (checkPadreApo1) checkPadreApo1.checked = false;
            copiarValores(apoRut2, apoNombres2, apoApellidos2, apoTelefono2, apoDireccion2, apoComuna2, padreRut, padreNombres, padreApellidos, padreTelefono, padreDireccion, padreComuna);
        }
    }

    [checkMadreApo1, checkPadreApo1, checkMadreApo2, checkPadreApo2].filter(Boolean).forEach(chk => {
        chk.addEventListener('change', sincronizarFormulario);
    });

    [apoRut, apoNombres, apoApellidos, apoTelefono, apoDireccion, apoComuna, apoRut2, apoNombres2, apoApellidos2, apoTelefono2, apoDireccion2, apoComuna2].filter(Boolean).forEach(inp => {
        inp.addEventListener('input', sincronizarFormulario);
    });

    // Formato y Validación de RUT
    function formatearRut(rut) {
        if (!rut) return '';
        rut = rut.toString().trim().toUpperCase().replace(/\./g, '').replace(/-/g, '').replace(/[^0-9K]/g, '');
        if (rut.length < 2) return rut;
        return `${rut.slice(0, -1)}-${rut.slice(-1)}`;
    }

    function validarRut(rut) {
        if (!rut) return false;
        rut = formatearRut(rut);
        if (!/^[0-9]+-[0-9Kk]$/.test(rut)) return false;

        let tmp = rut.split('-');
        let rutPuro = tmp[0];
        let digv = tmp[1].toLowerCase();
        let M = 0, S = 1;

        for (; rutPuro; rutPuro = Math.floor(rutPuro / 10)) {
            S = (S + (rutPuro % 10) * (9 - M++ % 6)) % 11;
        }
        return (S ? String(S - 1) : 'k') === digv;
    }

    document.querySelectorAll('.rut-input').forEach(input => {
        input.addEventListener('blur', function () {
            if (this.value.trim() === '') {
                this.classList.remove('is-invalid');
                return;
            }
            this.value = formatearRut(this.value);
            if (!validarRut(this.value)) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
    });

    // 4. Envío del Formulario (Petición POST)
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (document.querySelectorAll('.is-invalid').length > 0) {
            alert("Por favor, corrija los campos marcados en rojo (RUTs inválidos) antes de enviar.");
            return;
        }

        const payload = {
            cursoActual: getVal('cursoActual'),
            correoComprobante: getVal('apo1Correo', 'apoCorreo'),

            alumna: {
                rut: getVal('alumnaRut'),
                nombres: getVal('alumnaNombres'),
                apellidoPaterno: getVal('alumnaApellidoP'),
                apellidoMaterno: getVal('alumnaApellidoM'),
                fechaNacimiento: getVal('alumnaFechaNac'),
                nacionalidad: "Chilena",
                direccion: getVal('alumnaDireccion'),
                comuna: getVal('alumnaComuna'),
                viveCon: getVal('alumnaViveCon')
            },

            fichaMedica: {
                esAlergica: esAlergica ? (esAlergica.type === 'checkbox' ? esAlergica.checked : esAlergica.value === 'true') : false,
                detalleAlergias: getVal('detalleAlergias', 'medAlergiasDetalle') || null,
                tomaMedicamentos: tomaMedicamentos ? (tomaMedicamentos.type === 'checkbox' ? tomaMedicamentos.checked : tomaMedicamentos.value === 'true') : false,
                detalleMedicamentos: getVal('detalleMedicamentos', 'medMedicamentosDetalle') || null,
                condicionMedicaAdicional: getVal('condicionMedicaAdicional', 'medCondicion') || null
            },

            apoderadoTitular: {
                rut: getVal( 'apoRut'),
                nombres: getVal('apoNombres'),
                apellidos: getVal('apoApellidos'),
                parentesco: getVal('apoParentesco'),
                telefono: getVal('apoTelefono'),
                correo: getVal('apoCorreo'),
                direccion: getVal('apoDireccion'),
                comuna: getVal('apo1Comuna', 'apoComuna'),
                esMismoQueApoderado: getVal('checkMadreEsApoderado') === 'on' || getVal('checkPadreEsApoderado') === 'on'
            },

            apoderadoSuplente: {
                rut: getVal('apoRut2'),
                nombres: getVal('apoNombres2'),
                apellidos: getVal('apoApellidos2'),
                parentesco: getVal('apoParentesco2'),
                telefono: getVal('apoTelefono2'),
                correo: getVal('apoCorreo2'),
                direccion: getVal('apoDireccion2'),
                comuna: getVal('apoComuna2'),
                esMismoQueApoderado: getVal('checkMadreEsApoderado2') === 'on' || getVal('checkPadreEsApoderado2') === 'on'
            },

            madre: getVal('madreRut') ? {
                rut: getVal('madreRut'),
                nombres: getVal('madreNombres'),
                apellidos: getVal('madreApellidos'),
                parentesco: "MADRE",
                telefono: getVal('madreTelefono'),
                direccion: getVal('madreDireccion'),
                comuna: getVal('madreComuna')
            } : null,

            padre: getVal('padreRut') ? {
                rut: getVal('padreRut'),
                nombres: getVal('padreNombres'),
                apellidos: getVal('padreApellidos'),
                parentesco: "PADRE",
                telefono: getVal('padreTelefono'),
                direccion: getVal('padreDireccion'),
                comuna: getVal('padreComuna')
            } : null,

            autorizadoRetiro: {
                rut: getVal('retiraRut'),
                nombreCompleto: getVal('retiraNombreCompleto'),
                parentesco: getVal('retiraParentesco'),
                parentescoFurgon: getVal('retiraParentesco'),
                telefono: getVal('retiraTelefono')
            }
        };

        if (btnEnviar) {
            btnEnviar.disabled = true;
            btnEnviar.innerHTML = 'Enviando Pre-Matrícula...';
        }

        try {
            const respuesta = await fetch('/api/matriculas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (respuesta.ok) {
                const data = await respuesta.json();

                if (data.numeroMatricula) sessionStorage.setItem('numeroMatricula', data.numeroMatricula);
                if (data.fechaRegistro) sessionStorage.setItem('fechaRegistro', data.fechaRegistro);

                const correoDestino = data.correoComprobante || payload.correoComprobante || payload.apoderadoTitular.correoElectronico;
                sessionStorage.setItem('correoComprobante', correoDestino);

                alert('¡Pre-Matrícula enviada exitosamente!');
                window.location.href = 'comprobante.html';
            } else {
                let mensajeError = "Verifique los campos e intente de nuevo.";
                try {
                    const errData = await respuesta.json();
                    mensajeError = errData.mensaje || mensajeError;
                } catch (_) {
                    mensajeError = await respuesta.text();
                }
                alert("Error al procesar la matrícula: " + mensajeError);
            }
        } catch (error) {
            console.error("Error de red:", error);
            alert("Error de conexión con el servidor.");
        } finally {
            if (btnEnviar) {
                btnEnviar.disabled = false;
                btnEnviar.innerHTML = 'Enviar Pre-Matrícula';
            }
        }
    });
});