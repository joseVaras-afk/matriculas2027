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

    // --- REFERENCIAS DEL DOM ---
    // Apoderado Titular (Apoderado 1)
    const checkMadreApo1 = getEl('checkMadreEsApoderado');
    const checkPadreApo1 = getEl('checkPadreEsApoderado');
    const apo1 = {
        rut: getEl('apoRut'),
        nombres: getEl('apoNombres'),
        apellidos: getEl('apoApellidos'),
        parentesco: getEl('apoParentesco'),
        telefono: getEl('apoTelefono'),
        correo: getEl('apoCorreo'),
        direccion: getEl('apoDireccion'),
        comuna: getEl('apoComuna')
    };

    // Apoderado Suplente (Apoderado 2)
    const checkMadreApo2 = getEl('checkMadreEsApoderado2');
    const checkPadreApo2 = getEl('checkPadreEsApoderado2');
    const apo2 = {
        rut: getEl('apoRut2'),
        nombres: getEl('apoNombres2'),
        apellidos: getEl('apoApellidos2'),
        parentesco: getEl('apoParentesco2'),
        telefono: getEl('apoTelefono2'),
        correo: getEl('apoCorreo2'),
        direccion: getEl('apoDireccion2'),
        comuna: getEl('apoComuna2')
    };

    // Madre
    const madre = {
        rut: getEl('madreRut'),
        nombres: getEl('madreNombres'),
        apellidos: getEl('madreApellidos'),
        telefono: getEl('madreTelefono'),
        direccion: getEl('madreDireccion'),
        comuna: getEl('madreComuna')
    };

    // Padre
    const padre = {
        rut: getEl('padreRut'),
        nombres: getEl('padreNombres'),
        apellidos: getEl('padreApellidos'),
        telefono: getEl('padreTelefono'),
        direccion: getEl('padreDireccion'),
        comuna: getEl('padreComuna')
    };

    // Salud y Retiro
    const esAlergica = getEl('esAlergica');
    const detalleAlergias = getEl('detalleAlergias');
    const tomaMedicamentos = getEl('tomaMedicamentos');
    const detalleMedicamentos = getEl('detalleMedicamentos');
    const btnEnviar = getEl('btnEnviar');

    // --- LÓGICA DE COPIA Y SINCRONIZACIÓN ---
    function copyData(src, dst) {
        if (!src || !dst) return;
        if (src.rut && dst.rut) dst.rut.value = src.rut.value;
        if (src.nombres && dst.nombres) dst.nombres.value = src.nombres.value;
        if (src.apellidos && dst.apellidos) dst.apellidos.value = src.apellidos.value;
        if (src.telefono && dst.telefono) dst.telefono.value = src.telefono.value;
        if (src.direccion && dst.direccion) dst.direccion.value = src.direccion.value;
        if (src.comuna && dst.comuna) dst.comuna.value = src.comuna.value;
    }

    // Determina la dirección de la copia según cuál objeto tenga datos
    function smartCopy(objA, objB) {
        const aHasContent = (objA.rut && objA.rut.value) || (objA.nombres && objA.nombres.value);
        const bHasContent = (objB.rut && objB.rut.value) || (objB.nombres && objB.nombres.value);

        if (bHasContent && !aHasContent) {
            copyData(objB, objA);
        } else {
            copyData(objA, objB);
        }
    }

    function syncForm(triggerSource) {
        // Apo 1 - Madre
        if (checkMadreApo1 && checkMadreApo1.checked) {
            if (checkPadreApo1) checkPadreApo1.checked = false;
            if (checkMadreApo2) checkMadreApo2.checked = false;
            if (apo1.parentesco) apo1.parentesco.value = "MAMA";

            if (triggerSource === 'madre') copyData(madre, apo1);
            else if (triggerSource === 'apo1') copyData(apo1, madre);
            else smartCopy(apo1, madre);
        }

        // Apo 1 - Padre
        if (checkPadreApo1 && checkPadreApo1.checked) {
            if (checkMadreApo1) checkMadreApo1.checked = false;
            if (checkPadreApo2) checkPadreApo2.checked = false;
            if (apo1.parentesco) apo1.parentesco.value = "PAPA";

            if (triggerSource === 'padre') copyData(padre, apo1);
            else if (triggerSource === 'apo1') copyData(apo1, padre);
            else smartCopy(apo1, padre);
        }

        // Apo 2 - Madre
        if (checkMadreApo2 && checkMadreApo2.checked) {
            if (checkPadreApo2) checkPadreApo2.checked = false;
            if (checkMadreApo1) checkMadreApo1.checked = false;
            if (apo2.parentesco) apo2.parentesco.value = "MAMA";

            if (triggerSource === 'madre') copyData(madre, apo2);
            else if (triggerSource === 'apo2') copyData(apo2, madre);
            else smartCopy(apo2, madre);
        }

        // Apo 2 - Padre
        if (checkPadreApo2 && checkPadreApo2.checked) {
            if (checkMadreApo2) checkMadreApo2.checked = false;
            if (checkPadreApo1) checkPadreApo1.checked = false;
            if (apo2.parentesco) apo2.parentesco.value = "PAPA";

            if (triggerSource === 'padre') copyData(padre, apo2);
            else if (triggerSource === 'apo2') copyData(apo2, padre);
            else smartCopy(apo2, padre);
        }
    }

    // Escuchadores de cambio en Checkboxes
    if (checkMadreApo1) checkMadreApo1.addEventListener('change', () => syncForm('check'));
    if (checkPadreApo1) checkPadreApo1.addEventListener('change', () => syncForm('check'));
    if (checkMadreApo2) checkMadreApo2.addEventListener('change', () => syncForm('check'));
    if (checkPadreApo2) checkPadreApo2.addEventListener('change', () => syncForm('check'));

    // Escuchadores de escritura en campos
    Object.values(apo1).filter(Boolean).forEach(el => el.addEventListener('input', () => syncForm('apo1')));
    Object.values(apo2).filter(Boolean).forEach(el => el.addEventListener('input', () => syncForm('apo2')));
    Object.values(madre).filter(Boolean).forEach(el => el.addEventListener('input', () => syncForm('madre')));
    Object.values(padre).filter(Boolean).forEach(el => el.addEventListener('input', () => syncForm('padre')));

    // --- CONTROLES MÉDICOS ---
    if (esAlergica && detalleAlergias) {
        esAlergica.addEventListener('change', (e) => {
            const esTrue = e.target.value === 'true';
            detalleAlergias.disabled = !esTrue;
            if (!esTrue) detalleAlergias.value = '';
        });
    }

    if (tomaMedicamentos && detalleMedicamentos) {
        tomaMedicamentos.addEventListener('change', (e) => {
            const esTrue = e.target.value === 'true';
            detalleMedicamentos.disabled = !esTrue;
            if (!esTrue) detalleMedicamentos.value = '';
        });
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

    // --- ENVÍO DEL FORMULARIO ---
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (document.querySelectorAll('.is-invalid').length > 0) {
            alert("Por favor, corrija los campos marcados en rojo (RUTs inválidos) antes de enviar.");
            return;
        }

        const fechaStr = new Date().toISOString();

        const payload = {
            cursoActual: getVal('cursoActual'),
            correoComprobante: getVal('apoCorreo'),
            fechaHoraRegistro: fechaStr,
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
                esAlergica: getVal('esAlergica') === 'true',
                detalleAlergias: getVal('detalleAlergias') || null,
                tomaMedicamentos: getVal('tomaMedicamentos') === 'true',
                detalleMedicamentos: getVal('detalleMedicamentos') || null,
                condicionMedicaAdicional: getVal('condicionMedicaAdicional') || null
            },
            apoderadoTitular: {
                rut: getVal('apoRut'),
                nombres: getVal('apoNombres'),
                apellidos: getVal('apoApellidos'),
                parentesco: getVal('apoParentesco'),
                telefono: getVal('apoTelefono'),
                correo: getVal('apoCorreo'),
                direccion: getVal('apoDireccion'),
                comuna: getVal('apoComuna'),
                esMismoQueApoderado: (checkMadreApo1 && checkMadreApo1.checked) || (checkPadreApo1 && checkPadreApo1.checked)
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
                esMismoQueApoderado: (checkMadreApo2 && checkMadreApo2.checked) || (checkPadreApo2 && checkPadreApo2.checked)
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