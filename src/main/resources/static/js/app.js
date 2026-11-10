document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formularioMatricula');
    if (!form) return;

    function getEl(id) {
        return document.getElementById(id);
    }

    function getVal(id) {
        const el = getEl(id);
        return el ? el.value.trim() : '';
    }

    // --- ELEMENTOS DE CONTROL ---
    const esAlergica = getEl('esAlergica');
    const detalleAlergias = getEl('detalleAlergias');
    const tomaMedicamentos = getEl('tomaMedicamentos');
    const detalleMedicamentos = getEl('detalleMedicamentos');
    const etniaSiNo = getEl('etniaSiNo');
    const etnia = getEl('etnia');
    const tieneHermano = getEl('tieneHermano');
    const nomHer1 = getEl('nomHer1');
    const cursoHer1 = getEl('cursoHer1');
    const nomHer2 = getEl('nomHer2');
    const cursoHer2 = getEl('cursoHer2');
    const btnEnviar = getEl('btnEnviar');
    const checkMadreApo1 = getEl('checkMadreEsApoderado');
    const checkMadreApo2 = getEl('checkMadreEsApoderado2');

    // --- CONTROLES DINÁMICOS (ALERGIAS Y MEDICAMENTOS) ---
    if (esAlergica && detalleAlergias) {
        esAlergica.addEventListener('change', (e) => {
            const esTrue = e.target.value === 'true';
            detalleAlergias.disabled = !esTrue;
            if (!esTrue) {
                detalleAlergias.value = '';
                detalleAlergias.classList.remove('is-invalid');
            }
        });
    }

    if (tomaMedicamentos && detalleMedicamentos) {
        tomaMedicamentos.addEventListener('change', (e) => {
            const esTrue = e.target.value === 'true';
            detalleMedicamentos.disabled = !esTrue;
            if (!esTrue) {
                detalleMedicamentos.value = '';
                detalleMedicamentos.classList.remove('is-invalid');
            }
        });
    }

    // --- CONTROL PUEBLOS ORIGINARIOS ---
    if (etniaSiNo && etnia) {
        etniaSiNo.addEventListener('change', (e) => {
            const esTrue = e.target.value === 'true';
            etnia.disabled = !esTrue;
            if (!esTrue) {
                etnia.value = '';
                etnia.classList.remove('is-invalid');
            }
        });
    }

    // --- CONTROL HERMANOS EN EL COLEGIO ---
    if (tieneHermano) {
        const toggleHermanos = () => {
            const tiene = tieneHermano.value === 'SI';
            [nomHer1, cursoHer1, nomHer2, cursoHer2].forEach(el => {
                if (el) {
                    el.disabled = !tiene;
                    if (!tiene) {
                        el.value = '';
                        el.classList.remove('is-invalid');
                    }
                }
            });
        };
        tieneHermano.addEventListener('change', toggleHermanos);
        toggleHermanos(); // Inicializar estado al cargar la página
    }

    // --- FORMATO Y VALIDACIÓN DE RUT ---
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

    function procesarCampoRut(input) {
        if (!input) return true;
        const valor = input.value.trim();

        if (valor === '' && !input.hasAttribute('required')) {
            input.classList.remove('is-invalid', 'is-valid');
            return true;
        }

        if (valor === '' && input.hasAttribute('required')) {
            input.classList.add('is-invalid');
            input.classList.remove('is-valid');
            return false;
        }

        input.value = formatearRut(valor);
        const esValido = validarRut(input.value);

        if (!esValido) {
            input.classList.add('is-invalid');
            input.classList.remove('is-valid');
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        }
        return esValido;
    }

    document.querySelectorAll('.rut-input').forEach(input => {
        input.addEventListener('blur', () => procesarCampoRut(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('is-invalid')) {
                procesarCampoRut(input);
            }
        });
    });

    // --- SELECCIÓN ÚNICA EN VIVIENDA ---
    const checkboxesVivienda = document.querySelectorAll('.vivienda-checkbox');

    checkboxesVivienda.forEach(chk => {
        chk.addEventListener('change', function () {
            if (this.checked) {
                checkboxesVivienda.forEach(otherChk => {
                    if (otherChk !== this) {
                        otherChk.checked = false;
                    }
                });
            }
        });
    });

    function obtenerDatosVivienda() {
        const seleccionada = Array.from(checkboxesVivienda).find(c => c.checked);
        const valor = seleccionada ? seleccionada.value : null;

        return {
            propia: valor === 'propia' ? 'SI' : '',
            arrendada: valor === 'arrendada' ? 'SI' : '',
            cedida: valor === 'cedida' ? 'SI' : '',
            allegado: valor === 'allegado' ? 'SI' : ''
        };
    }

    // --- ENVÍO DEL FORMULARIO ---
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Activar validación visual nativa
        form.classList.add('was-validated');

        // 2. Validar campos de RUT obligatorios y opcionales con datos
        let hayRutInvalido = false;
        document.querySelectorAll('.rut-input').forEach(input => {
            if (input.hasAttribute('required') || input.value.trim() !== '') {
                const valido = procesarCampoRut(input);
                if (!valido) hayRutInvalido = true;
            }
        });

        // 3. Verificar errores antes de enviar
        if (!form.checkValidity() || hayRutInvalido || document.querySelectorAll('.is-invalid').length > 0) {
            const primerError = form.querySelector(':invalid, .is-invalid');
            if (primerError) {
                primerError.focus();
                primerError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
            return;
        }

        const fechaStr = new Date().toISOString();
        const vivienda = obtenerDatosVivienda();

        // 4. Construir objeto Payload para el Backend
        const payload = {
            cursoActual: getVal('cursoActual'),
            correoComprobante: getVal('apoCorreo'),
            fechaHoraRegistro: fechaStr,
            numeroMatricula: getVal('numeroMatricula'),

            alumna: {
                rut: getVal('alumnaRut'),
                nombres: getVal('alumnaNombres'),
                apellidoPaterno: getVal('alumnaApellidoP'),
                apellidoMaterno: getVal('alumnaApellidoM'),
                fechaNacimiento: getVal('alumnaFechaNac'),
                alumnaNacionalidad: getVal('alumnaNacionalidad') || "CHILENA",
                direccion: getVal('alumnaDireccion'),
                alumnaVilla: getVal('alumnaVilla'),
                comuna: getVal('alumnaComuna'),
                viveCon: getVal('alumnaViveCon'),
                etniaSiNo: getVal('etniaSiNo') === 'true',
                etnia: getVal('etnia'),
                procedencia: getVal('procedencia'),
                nivelesRepetidos: getVal('nivelesRepetidos'),
                necesidadesEspeciales: getVal('necesidadesEspeciales')
            },

            apoderadoTitular: {
                rut: getVal('apoRut'),
                nombres: getVal('apoNombres'),
                apellidos: getVal('apoApellidos'),
                parentesco: getVal('apoParentesco'),
                telefono: getVal('apoTelefono'),
                correo: getVal('apoCorreo'),
                direccion: getVal('apoDireccion'),
                apoVilla: getVal('apoVilla'),
                comuna: getVal('apoComuna'),
                apoEstCivil: getVal('apoEstCivil'),
                apoFechaNac: getVal('apoFechaNac'),
                edad: getVal('edadApo') ? parseInt(getVal('edadApo')) : null,
                generoApo: getVal('generoApo'),
                nivEstudioApo: getVal('nivEstudioApo'),
                ocupacionApo: getVal('ocupacionApo'),
                jornadaApo: getVal('jornadaApo'),
                telLabApo: getVal('telLabApo'),
                direcLabApo: getVal('direcLabApo'),
                esMismoQueApoderado: checkMadreApo1 ? checkMadreApo1.checked : false
            },

            apoderadoSuplente: getVal('apoRut2') ? {
                rut: getVal('apoRut2'),
                nombres: getVal('apoNombres2'),
                apellidos: getVal('apoApellidos2'),
                parentesco: getVal('apoParentesco2'),
                telefono: getVal('apoTelefono2'),
                correo: getVal('apoCorreo2'),
                direccion: getVal('apoDireccion2'),
                apoVilla: getVal('apoVilla2'),
                comuna: getVal('apoComuna2'),
                apoEstCivil: getVal('apoEstCivil2'),
                apoFechaNac: getVal('apoFechaNac2'),
                edad: getVal('edadApo2') ? parseInt(getVal('edadApo2')) : null,
                generoApo: getVal('generoApo2'),
                nivEstudioApo: getVal('nivEstudioApo2'),
                ocupacionApo: getVal('ocupacionApo2'),
                jornadaApo: getVal('jornadaApo2'),
                telLabApo: getVal('telLabApo2'),
                direcLabApo: getVal('direcLabApo2'),
                esMismoQueApoderado: checkMadreApo2 ? checkMadreApo2.checked : false
            } : null,

            datosFamiliares: {
                nIntegrantes: getVal('nIntegrantes'),
                nHermanos: getVal('nHermanos'),
                tieneHermano: getVal('tieneHermano'),
                nomHer1: getVal('nomHer1'),
                cursoHer1: getVal('cursoHer1'),
                nomHer2: getVal('nomHer2'),
                cursoHer2: getVal('cursoHer2'),
                propia: vivienda.propia,
                arrendada: vivienda.arrendada,
                cedida: vivienda.cedida,
                allegado: vivienda.allegado,
                redApoyo: getVal('redApoyo')
            },

            fichaMedica: {
                esAlergica: getVal('esAlergica') === 'true',
                detalleAlergias: getVal('detalleAlergias') || null,
                tomaMedicamentos: getVal('tomaMedicamentos') === 'true',
                detalleMedicamentos: getVal('detalleMedicamentos') || null,
                prevsion: getVal('prevsion'),
                cesfam: getVal('cesfam'),
                personaEmergencia: getVal('personaEmergencia'),
                telEmergencia: getVal('telEmergencia'),
                parentescoEmergencia: getVal('parentescoEmergencia'),
                condicionMedicaAdicional: getVal('condicionMedicaAdicional') || null
            },

            autorizado1: getVal('retiraRut1') ? {
                rut: getVal('retiraRut1'),
                nombreCompleto: getVal('retiraNombreCompleto1'),
                parentesco: getVal('retiraParentesco1'),
                telefono: getVal('retiraTelefono1')
            } : null,

            autorizado2: getVal('retiraRut2') ? {
                rut: getVal('retiraRut2'),
                nombreCompleto: getVal('retiraNombreCompleto2'),
                parentesco: getVal('retiraParentesco2'),
                telefono: getVal('retiraTelefono2')
            } : null,

            autorizado3: getVal('retiraRut3') ? {
                rut: getVal('retiraRut3'),
                nombreCompleto: getVal('retiraNombreCompleto3'),
                parentesco: getVal('retiraParentesco3'),
                telefono: getVal('retiraTelefono3')
            } : null
        };

        if (btnEnviar) {
            btnEnviar.disabled = true;
            btnEnviar.innerHTML = 'Enviando Pre-Matrícula...';
        }

        try {
            let respuesta = await fetch('/api/matriculas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (respuesta.status === 404) {
                respuesta = await fetch('/api/matricula', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }

            if (respuesta.ok) {
                const data = await respuesta.json();

                if (data.numeroMatricula) sessionStorage.setItem('numeroMatricula', data.numeroMatricula);
                if (data.fechaRegistro) sessionStorage.setItem('fechaRegistro', data.fechaRegistro);

                const correoDestino = data.correoComprobante || payload.correoComprobante || payload.apoderadoTitular.correo;
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