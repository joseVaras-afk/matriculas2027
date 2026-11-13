package com.Matricula2027.matricula2027.service;

import com.Matricula2027.matricula2027.dto.FichaDto;
import com.Matricula2027.matricula2027.dto.MatriculaListadoDTO;
import com.Matricula2027.matricula2027.dto.MatriculaRequestDTO;
import com.Matricula2027.matricula2027.dto.MatriculaRequestDTO.FamiliarDTO;
import com.Matricula2027.matricula2027.entity.*;
import com.Matricula2027.matricula2027.repository.AlumnaRepository;
import com.Matricula2027.matricula2027.repository.FamiliarRepository;
import com.Matricula2027.matricula2027.repository.MatriculaRepository;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final FamiliarRepository familiarRepository;
    private final AlumnaRepository alumnaRepository;

    @Transactional
    public Matricula registrarMatricula(MatriculaRequestDTO dto) {
        String rutAlumna = dto.getAlumna().getRut().trim().toUpperCase();
        if (alumnaRepository.existsByRut(rutAlumna)) {
            throw new IllegalArgumentException("Ya existe una alumna registrada con el RUT: " + rutAlumna);
        }

        Familiar apoderado = obtenerOCrearFamiliar(dto.getApoderadoTitular());
        Familiar apoderadoSuplente = obtenerOCrearFamiliar(dto.getApoderadoSuplente());

        Alumna alumna = new Alumna();
        alumna.setRut(rutAlumna);
        alumna.setNombres(dto.getAlumna().getNombres());
        alumna.setApellidoPaterno(dto.getAlumna().getApellidoPaterno());
        alumna.setApellidoMaterno(dto.getAlumna().getApellidoMaterno());
        alumna.setFechaNacimiento(dto.getAlumna().getFechaNacimiento());
        alumna.setDireccion(dto.getAlumna().getDireccion());
        alumna.setComuna(dto.getAlumna().getComuna());
        alumna.setViveCon(dto.getAlumna().getViveCon());
        alumna.setAlumnaVilla(dto.getAlumna().getAlumnaVilla());
        alumna.setNacionalidad(dto.getAlumna().getAlumnaNacionalidad());
        alumna.setEtniaSiNo(
                dto.getAlumna().getEtniaSiNo() != null ? (dto.getAlumna().getEtniaSiNo() ? "SI" : "NO") : "NO");
        alumna.setEtnia(dto.getAlumna().getEtnia());
        alumna.setProcedencia(dto.getAlumna().getProcedencia());
        alumna.setNivelesRepetidos(dto.getAlumna().getNivelesRepetidos());
        alumna.setNecesidadesEspeciales(dto.getAlumna().getNecesidadesEspeciales());

        Matricula matricula = new Matricula();
        matricula.setCursoActual(dto.getCursoActual());
        matricula.setApoderadoTitular(apoderado);
        matricula.setApoderadoSuplente(apoderadoSuplente);
        matricula.setFechaHoraRegistro(LocalDateTime.now());

        // 1. Folio temporal único (máx. 17 caracteres) para evitar colisiones de índice
        // UNIQUE en TiDB
        String folioTemporal = "T-" + Long.toString(System.nanoTime(), 36) + "-"
                + java.util.concurrent.ThreadLocalRandom.current().nextInt(100, 999);
        matricula.setNumeroMatricula(folioTemporal);

        matricula.setEstadoFicha("PENDIENTE");
        matricula.setCorreoComprobante(
                dto.getApoderadoTitular() != null && dto.getApoderadoTitular().getCorreo() != null
                        ? dto.getApoderadoTitular().getCorreo()
                        : dto.getCorreoComprobante());
        matricula.setCursoPostulacion(calcularCursoSiguiente(dto.getCursoActual()));
        matricula.setAlumna(alumna);

        matricula.setRetSola(dto.getRetSola());
        matricula.setCondCont(dto.getCondContratado());
        matricula.setCondRun(dto.getCondRun());
        matricula.setCondTel(dto.getCondTel());

        // Personas autorizadas de retiro
        if (dto.getAutorizado1() != null) {
            AutorizadoRetiro aut1 = new AutorizadoRetiro();
            aut1.setRut(dto.getAutorizado1().getRut());
            aut1.setNombreCompleto(dto.getAutorizado1().getNombreCompleto());
            aut1.setParentescoFurgon(dto.getAutorizado1().getParentesco());
            aut1.setTelefono(dto.getAutorizado1().getTelefono());
            matricula.setAutorizado1(aut1);
        }
        if (dto.getAutorizado2() != null) {
            AutorizadoRetiro aut2 = new AutorizadoRetiro();
            aut2.setRut(dto.getAutorizado2().getRut());
            aut2.setNombreCompleto(dto.getAutorizado2().getNombreCompleto());
            aut2.setParentescoFurgon(dto.getAutorizado2().getParentesco());
            aut2.setTelefono(dto.getAutorizado2().getTelefono());
            matricula.setAutorizado2(aut2);
        }
        if (dto.getAutorizado3() != null) {
            AutorizadoRetiro aut3 = new AutorizadoRetiro();
            aut3.setRut(dto.getAutorizado3().getRut());
            aut3.setNombreCompleto(dto.getAutorizado3().getNombreCompleto());
            aut3.setParentescoFurgon(dto.getAutorizado3().getParentesco());
            aut3.setTelefono(dto.getAutorizado3().getTelefono());
            matricula.setAutorizado3(aut3);
        }

        // Datos Familiares
        if (dto.getDatosFamiliares() != null) {
            DatosFamiliares df = new DatosFamiliares();
            df.setNIntegrantes(parseIntegerSafe(dto.getDatosFamiliares().getNIntegrantes()));
            df.setNHermanos(parseIntegerSafe(dto.getDatosFamiliares().getNHermanos()));
            df.setTieneHermano(dto.getDatosFamiliares().getTieneHermano());
            df.setNomHer1(dto.getDatosFamiliares().getNomHer1());
            df.setNomHer2(dto.getDatosFamiliares().getNomHer2());
            df.setCursoHer1(dto.getDatosFamiliares().getCursoHer1());
            df.setCursoHer2(dto.getDatosFamiliares().getCursoHer2());
            df.setRedApoyo(dto.getDatosFamiliares().getRedApoyo());
            df.setPropia(dto.getDatosFamiliares().getPropia());
            df.setArrendada(dto.getDatosFamiliares().getArrendada());
            df.setCedida(dto.getDatosFamiliares().getCedida());
            df.setAllegado(dto.getDatosFamiliares().getAllegado());
            matricula.setDatosFamiliares(df);
        }

        // Ficha Médica
        if (dto.getFichaMedica() != null) {
            FichaMedica fichaMedica = new FichaMedica();
            fichaMedica.setEsAlergica(dto.getFichaMedica().getEsAlergica());
            fichaMedica.setDetalleAlergias(dto.getFichaMedica().getDetalleAlergias());
            fichaMedica.setTomaMedicamentos(dto.getFichaMedica().getTomaMedicamentos());
            fichaMedica.setDetalleMedicamentos(dto.getFichaMedica().getDetalleMedicamentos());
            fichaMedica.setCondicionMedicaAdicional(dto.getFichaMedica().getCondicionMedicaAdicional());
            fichaMedica.setEnfermedad(dto.getFichaMedica().getEnfermedad());
            fichaMedica.setEnfermedadCual(dto.getFichaMedica().getEnfermedadCual());
            fichaMedica.setTratamiento(dto.getFichaMedica().getTratamiento());
            fichaMedica.setContramedicacion(dto.getFichaMedica().getContramedicacion());
            fichaMedica.setPrevsion(dto.getFichaMedica().getPrevsion());
            fichaMedica.setCesfam(dto.getFichaMedica().getCesfam());
            fichaMedica.setPersonaEmergencia(dto.getFichaMedica().getPersonaEmergencia());
            fichaMedica.setTelEmergencia(dto.getFichaMedica().getTelEmergencia());
            fichaMedica.setParentescoEmergencia(dto.getFichaMedica().getParentescoEmergencia());
            matricula.setFichaMedica(fichaMedica);
        }

        // 2. Insertar para obtener el ID autoincremental garantizado por TiDB
        Matricula matriculaGuardada = matriculaRepository.saveAndFlush(matricula);

        // 3. Asignar el número de matrícula correlativo definitivo basado en su ID
        // único
        String numeroDefinitivo = String.format("2027-MAT-%04d", matriculaGuardada.getId());
        matriculaGuardada.setNumeroMatricula(numeroDefinitivo);

        return matriculaRepository.saveAndFlush(matriculaGuardada);
    }

    private Familiar obtenerOCrearFamiliar(FamiliarDTO dto) {
        if (dto == null || dto.getRut() == null || dto.getRut().isBlank()) {
            return null;
        }

        return familiarRepository.findByRut(dto.getRut())
                .map(familiarExistente -> {
                    if (dto.getNombres() != null)
                        familiarExistente.setNombres(dto.getNombres());
                    if (dto.getApellidos() != null)
                        familiarExistente.setApellidos(dto.getApellidos());
                    if (dto.getParentesco() != null)
                        familiarExistente.setParentesco(dto.getParentesco());
                    if (dto.getDireccion() != null)
                        familiarExistente.setDireccion(dto.getDireccion());
                    if (dto.getComuna() != null)
                        familiarExistente.setComuna(dto.getComuna());
                    if (dto.getTelefono() != null)
                        familiarExistente.setTelefono(dto.getTelefono());
                    if (dto.getCorreo() != null)
                        familiarExistente.setCorreo(dto.getCorreo());
                    if (dto.getEdad() != null)
                        familiarExistente.setEdad(dto.getEdad());
                    if (dto.getApoFechaNac() != null)
                        familiarExistente.setApoFechaNac(dto.getApoFechaNac());
                    if (dto.getGeneroApo() != null)
                        familiarExistente.setApoGenero(dto.getGeneroApo());
                    if (dto.getApoVilla() != null)
                        familiarExistente.setApoVilla(dto.getApoVilla());
                    if (dto.getNivEstudioApo() != null)
                        familiarExistente.setNivEstudioApo(dto.getNivEstudioApo());
                    if (dto.getOcupacionApo() != null)
                        familiarExistente.setOcupacionApo(dto.getOcupacionApo());
                    if (dto.getJornadaApo() != null)
                        familiarExistente.setJornadaApo(dto.getJornadaApo());
                    if (dto.getTelLabApo() != null)
                        familiarExistente.setTelLabApo(dto.getTelLabApo());
                    if (dto.getDirecLabApo() != null)
                        familiarExistente.setDirecLabApo(dto.getDirecLabApo());
                    if (dto.getApoEstCivil() != null)
                        familiarExistente.setApoEstCivil(dto.getApoEstCivil());
                    return familiarRepository.save(familiarExistente);
                })
                .orElseGet(() -> {
                    Familiar nuevo = new Familiar();
                    nuevo.setRut(dto.getRut());
                    nuevo.setNombres(dto.getNombres());
                    nuevo.setApellidos(dto.getApellidos());
                    nuevo.setTelefono(dto.getTelefono());
                    nuevo.setCorreo(dto.getCorreo());
                    nuevo.setParentesco(dto.getParentesco());
                    nuevo.setDireccion(dto.getDireccion());
                    nuevo.setComuna(dto.getComuna());
                    nuevo.setEdad(dto.getEdad());
                    nuevo.setApoFechaNac(dto.getApoFechaNac());
                    nuevo.setApoGenero(dto.getGeneroApo());
                    nuevo.setApoVilla(dto.getApoVilla());
                    nuevo.setNivEstudioApo(dto.getNivEstudioApo());
                    nuevo.setOcupacionApo(dto.getOcupacionApo());
                    nuevo.setJornadaApo(dto.getJornadaApo());
                    nuevo.setTelLabApo(dto.getTelLabApo());
                    nuevo.setDirecLabApo(dto.getDirecLabApo());
                    nuevo.setApoEstCivil(dto.getApoEstCivil());
                    return familiarRepository.save(nuevo);
                });
    }

    private String generarNumeroMatricula() {
        long cantidadActual = matriculaRepository.count();
        return String.format("2027-MAT-%04d", cantidadActual + 1);
    }

    @Transactional(readOnly = true)
    public List<Matricula> obtenerTodas() {
        return matriculaRepository.findAllConRelaciones();
    }

    @Transactional(readOnly = true)
    public List<MatriculaListadoDTO> listarMatriculasResumen() {
        return matriculaRepository.obtenerListadoResumen();
    }

    @Transactional(readOnly = true)
    public List<Matricula> obtenerPorCurso(String curso) {
        return matriculaRepository.findByCursoActual(curso);
    }

    @Transactional(readOnly = true)
    public Matricula obtenerPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la matrícula con ID: " + id));
    }

    @Transactional
    public void actualizarEstado(Long id, String nuevoEstado) {
        matriculaRepository.actualizarEstadoDirecto(id, nuevoEstado);
    }

    @Transactional(readOnly = true)
    public byte[] generarFichaDocx(Matricula m) {
        String rutaPlantilla = "templates/plantilla_fichaV2.docx";

        try (InputStream in = new ClassPathResource(rutaPlantilla).getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IXDocReport report = XDocReportRegistry.getRegistry()
                    .loadReport(in, TemplateEngineKind.Velocity);

            FichaDto dto = mapear(m);
            IContext ctx = report.createContext();

            // 1. Encabezado y Matrícula
            ctx.put("idMatricula", dto.getNumeroMatricula());
            ctx.put("curso2027", dto.getCurso2027());
            ctx.put("curso", dto.getCurso());

            // 2. Alumna
            ctx.put("nombreAlumna", dto.getNombreAlumna());
            ctx.put("apellidoPAlumna", dto.getApellidoPAlumna());
            ctx.put("apellidoMAlumna", dto.getApellidoMAlumna());
            ctx.put("rutAlumna", dto.getRutAlumna());
            ctx.put("alumnoFechaN", dto.getAlumnoFechaN());
            ctx.put("alumnaDireccion", dto.getAlumnaDireccion());
            ctx.put("aluComuna", dto.getAluComuna());
            ctx.put("viveCon", dto.getViveCon());
            ctx.put("aluNomCom", dto.getAluNomCom());
            ctx.put("alumnaVilla", dto.getAlumnaVilla());
            ctx.put("alumnaNacionalidad", dto.getAlumnaNacionalidad());
            ctx.put("etniaSiNo", dto.getEtniaSiNo());
            ctx.put("etnia", dto.getEtnia());
            ctx.put("procedencia", dto.getProcedencia());
            ctx.put("nivelesRepetidos", dto.getNivelesRepetidos());
            ctx.put("necesidadesEspeciales", dto.getNecesidadesEspeciales());

            // 3. Apoderado Titular
            ctx.put("apoNombre", dto.getApoNombre());
            ctx.put("apoApellidos", dto.getApoApellidos());
            ctx.put("apoRut", dto.getApoRut());
            ctx.put("apoParentesco", dto.getApoParentesco());
            ctx.put("apoTelefono", dto.getApoTelefono());
            ctx.put("apoCorreo", dto.getApoCorreo());
            ctx.put("apoDireccion", dto.getApoDireccion());
            ctx.put("apoComuna", dto.getApoComuna());
            ctx.put("apoNomCom", dto.getApoNomCom());
            ctx.put("edadApo", dto.getEdadApo());
            ctx.put("apoFechaNac", dto.getApoFechaNac());
            ctx.put("generoApo", dto.getGeneroApo());
            ctx.put("apoVilla", dto.getApoVilla());
            ctx.put("nivEstudioApo", dto.getNivEstudioApo());
            ctx.put("ocupacionApo", dto.getOcupacionApo());
            ctx.put("jornadaApo", dto.getJornadaApo());
            ctx.put("telLabApo", dto.getTelLabApo());
            ctx.put("direcLabApo", dto.getDirecLabApo());
            ctx.put("apoEstCivil", dto.getApoEstCivil());

            // 4. Apoderado Suplente
            ctx.put("apo2Nombre", dto.getApo2Nombre());
            ctx.put("apo2Apellidos", dto.getApo2Apellidos());
            ctx.put("apo2Rut", dto.getApo2Rut());
            ctx.put("apo2Parentesco", dto.getApo2Parentesco());
            ctx.put("apo2Telefono", dto.getApo2Telefono());
            ctx.put("apo2Correo", dto.getApo2Correo());
            ctx.put("apo2Dire", dto.getApo2Dire());
            ctx.put("apo2Comuna", dto.getApo2Comuna());
            ctx.put("edadApo2", dto.getEdadApo2());
            ctx.put("fechaNacApo2", dto.getFechaNacApo2());
            ctx.put("generoApo2", dto.getGeneroApo2());
            ctx.put("apo2EstCivil", dto.getApo2EstCivil());
            ctx.put("apo2NivEst", dto.getApo2NivEst());
            ctx.put("apo2Ocupacion", dto.getApo2Ocupacion());
            ctx.put("apo2Jornada", dto.getApo2Jornada());
            ctx.put("apo2TelLab", dto.getApo2TelLab());
            ctx.put("apo2DirecLab", dto.getApo2DirecLab());

            // 5. Retiro
            ctx.put("nombreRet1", dto.getNombreRet1());
            ctx.put("rutRet1", dto.getRutRetira1());
            ctx.put("parenRet1", dto.getParenRet1());
            ctx.put("telRet1", dto.getTelRet1());

            ctx.put("nombreRet2", dto.getNombreRet2());
            ctx.put("rutRet2", dto.getRutRetira2());
            ctx.put("parenRet2", dto.getParenRet2());
            ctx.put("telRet2", dto.getTelRet2());

            ctx.put("nombreRet3", dto.getNombreRet3());
            ctx.put("rutRet3", dto.getRutRetira3());
            ctx.put("parenRet3", dto.getParenRet3());
            ctx.put("telRet3", dto.getTelRet3());

            ctx.put("retSola", dto.getRetSola());
            ctx.put("condCont", dto.getCondCont());
            ctx.put("condRun", dto.getCondRun());
            ctx.put("condTel", dto.getCondTel());

            // 6. Datos Familiares
            ctx.put("nIntegrantes", dto.getNIntegrantes());
            ctx.put("nHermanos", dto.getNHermanos());
            ctx.put("tieneHermano", dto.getTieneHermano());
            ctx.put("nomHer1", dto.getNomHer1());
            ctx.put("nomHer2", dto.getNomHer2());
            ctx.put("cursoHer1", dto.getCursoHer1());
            ctx.put("cursoHer2", dto.getCursoHer2());
            ctx.put("redApoyo", dto.getRedApoyo());
            ctx.put("propia", dto.getPropia());
            ctx.put("arrendada", dto.getArrendada());
            ctx.put("cedida", dto.getCedida());
            ctx.put("allegado", dto.getAllegado());

            // 7. Ficha Médica
            ctx.put("esAlergica", dto.getEsAlergica());
            ctx.put("detalleAlergia", dto.getDetalleAlergia());
            ctx.put("tomaMed", dto.getTomaMed());
            ctx.put("detalleMed", dto.getDetalleMed());
            ctx.put("condicionMed", dto.getCondicionMed());
            ctx.put("enfermedad", dto.getEnfermedad());
            ctx.put("enfermedadCual", dto.getEnfermedadCual());
            ctx.put("tratamiento", dto.getTratamiento());
            ctx.put("contramedicacion", dto.getContramedicacion());
            ctx.put("prevsion", dto.getPrevsion());
            ctx.put("cesfam", dto.getCesfam());
            ctx.put("personaEmergencia", dto.getPersonaEmergencia());
            ctx.put("telEmergencia", dto.getTelEmergencia());
            ctx.put("parentescoEmergencia", dto.getParentescoEmergencia());

            report.process(ctx, out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando ficha DOCX", e);
        }
    }

    public FichaDto mapear(Matricula m) {
        if (m == null) {
            return new FichaDto();
        }

        FichaDto dto = new FichaDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // 1. Datos Matrícula
        dto.setFechaHoraRegistro(m.getFechaHoraRegistro() != null ? m.getFechaHoraRegistro().format(formatter) : "");
        dto.setNumeroMatricula(defaultString(m.getNumeroMatricula()));
        dto.setCurso(defaultString(m.getCursoActual()));
        dto.setCurso2027(calcularCursoSiguiente(m.getCursoActual()));
        dto.setCorreoComprobante(defaultString(m.getCorreoComprobante()));

        // 2. Alumna
        Alumna alu = m.getAlumna();
        if (alu != null) {
            dto.setNombreAlumna(defaultString(alu.getNombres()));
            dto.setApellidoPAlumna(defaultString(alu.getApellidoPaterno()));
            dto.setApellidoMAlumna(defaultString(alu.getApellidoMaterno()));
            dto.setRutAlumna(defaultString(alu.getRut()));
            dto.setAlumnoFechaN(alu.getFechaNacimiento() != null ? alu.getFechaNacimiento().toString() : "");
            dto.setAlumnaDireccion(defaultString(alu.getDireccion()));
            dto.setAluComuna(defaultString(alu.getComuna()));
            dto.setViveCon(defaultString(alu.getViveCon()));
            dto.setAlumnaVilla(defaultString(alu.getAlumnaVilla()));
            dto.setAlumnaNacionalidad(defaultString(alu.getNacionalidad()));
            dto.setEtnia(defaultString(alu.getEtnia()));
            dto.setEtniaSiNo(defaultString(alu.getEtniaSiNo()));
            dto.setProcedencia(defaultString(alu.getProcedencia()));
            dto.setNivelesRepetidos(defaultString(alu.getNivelesRepetidos()));
            dto.setNecesidadesEspeciales(defaultString(alu.getNecesidadesEspeciales()));

            String aluNomCom = String.format("%s %s %s",
                    defaultString(alu.getNombres()),
                    defaultString(alu.getApellidoPaterno()),
                    defaultString(alu.getApellidoMaterno())).trim();
            dto.setAluNomCom(aluNomCom);
        }

        // 3. Apoderado Titular
        Familiar apo = m.getApoderadoTitular();
        if (apo != null) {
            dto.setApoNombre(defaultString(apo.getNombres()));
            dto.setApoApellidos(defaultString(apo.getApellidos()));
            dto.setApoRut(defaultString(apo.getRut()));
            dto.setApoParentesco(defaultString(apo.getParentesco()));
            dto.setApoTelefono(defaultString(apo.getTelefono()));
            dto.setApoCorreo(defaultString(apo.getCorreo()));
            dto.setApoDireccion(defaultString(apo.getDireccion()));
            dto.setApoComuna(defaultString(apo.getComuna()));

            dto.setEdadApo(apo.getEdad());
            dto.setApoFechaNac(apo.getApoFechaNac());
            dto.setGeneroApo(defaultString(apo.getApoGenero()));
            dto.setApoVilla(defaultString(apo.getApoVilla()));
            dto.setNivEstudioApo(defaultString(apo.getNivEstudioApo()));
            dto.setOcupacionApo(defaultString(apo.getOcupacionApo()));
            dto.setJornadaApo(defaultString(apo.getJornadaApo()));
            dto.setTelLabApo(defaultString(apo.getTelLabApo()));
            dto.setDirecLabApo(defaultString(apo.getDirecLabApo()));
            dto.setApoEstCivil(defaultString(apo.getApoEstCivil()));
            dto.setApoNivEst(defaultString(apo.getNivEstudioApo()));
            dto.setApoOcupacion(defaultString(apo.getOcupacionApo()));
            dto.setApoJornada(defaultString(apo.getJornadaApo()));
            dto.setApoTelLab(defaultString(apo.getTelLabApo()));
            dto.setApoDirecLab(defaultString(apo.getDirecLabApo()));

            String apoNomCom = String
                    .format("%s %s", defaultString(apo.getNombres()), defaultString(apo.getApellidos())).trim();
            dto.setApoNomCom(apoNomCom);
        }

        // 4. Apoderado Suplente
        Familiar apo2 = m.getApoderadoSuplente();
        if (apo2 != null) {
            dto.setApo2Nombre(defaultString(apo2.getNombres()));
            dto.setApo2Apellidos(defaultString(apo2.getApellidos()));
            dto.setApo2Rut(defaultString(apo2.getRut()));
            dto.setApo2Parentesco(defaultString(apo2.getParentesco()));
            dto.setApo2Telefono(defaultString(apo2.getTelefono()));
            dto.setApo2Correo(defaultString(apo2.getCorreo()));
            dto.setApo2Dire(defaultString(apo2.getDireccion()));
            dto.setApo2Comuna(defaultString(apo2.getComuna()));
            dto.setApo2NivEst(defaultString(apo2.getNivEstudioApo()));
            dto.setApo2Ocupacion(defaultString(apo2.getOcupacionApo()));
            dto.setApo2Jornada(defaultString(apo2.getJornadaApo()));
            dto.setApo2TelLab(defaultString(apo2.getTelLabApo()));
            dto.setApo2DirecLab(defaultString(apo2.getDirecLabApo()));
            dto.setEdadApo2(apo2.getEdad());
            dto.setFechaNacApo2(apo2.getApoFechaNac());
            dto.setGeneroApo2(defaultString(apo2.getApoGenero()));
            dto.setApo2EstCivil(defaultString(apo2.getApoEstCivil()));
            dto.setApo2Villa(defaultString(apo2.getApoVilla()));
        }

        // 5. Retiro
        AutorizadoRetiro ret1 = m.getAutorizado1();
        if (ret1 != null) {
            dto.setNombreRet1(defaultString(ret1.getNombreCompleto()));
            dto.setRutRetira1(defaultString(ret1.getRut()));
            dto.setParenRet1(defaultString(ret1.getParentescoFurgon()));
            dto.setTelRet1(defaultString(ret1.getTelefono()));
        }

        AutorizadoRetiro ret2 = m.getAutorizado2();
        if (ret2 != null) {
            dto.setNombreRet2(defaultString(ret2.getNombreCompleto()));
            dto.setRutRetira2(defaultString(ret2.getRut()));
            dto.setParenRet2(defaultString(ret2.getParentescoFurgon()));
            dto.setTelRet2(defaultString(ret2.getTelefono()));
        }

        AutorizadoRetiro ret3 = m.getAutorizado3();
        if (ret3 != null) {
            dto.setNombreRet3(defaultString(ret3.getNombreCompleto()));
            dto.setRutRetira3(defaultString(ret3.getRut()));
            dto.setParenRet3(defaultString(ret3.getParentescoFurgon()));
            dto.setTelRet3(defaultString(ret3.getTelefono()));
        }

        dto.setRetSola(defaultString(m.getRetSola()));
        dto.setCondCont(defaultString(m.getCondCont()));
        dto.setCondRun(defaultString(m.getCondRun()));
        dto.setCondTel(defaultString(m.getCondTel()));

        // 6. Datos Familiares
        DatosFamiliares df = m.getDatosFamiliares();
        if (df != null) {
            dto.setNIntegrantes(df.getNIntegrantes());
            dto.setNHermanos(df.getNHermanos());
            dto.setTieneHermano(defaultString(df.getTieneHermano()));
            dto.setNomHer1(defaultString(df.getNomHer1()));
            dto.setNomHer2(defaultString(df.getNomHer2()));
            dto.setCursoHer1(defaultString(df.getCursoHer1()));
            dto.setCursoHer2(defaultString(df.getCursoHer2()));
            dto.setRedApoyo(defaultString(df.getRedApoyo()));
            dto.setPropia(defaultString(df.getPropia()));
            dto.setArrendada(defaultString(df.getArrendada()));
            dto.setCedida(defaultString(df.getCedida()));
            dto.setAllegado(defaultString(df.getAllegado()));
        }

        // 7. Ficha Médica
        FichaMedica fm = m.getFichaMedica();
        if (fm != null) {
            dto.setEsAlergica(Boolean.TRUE.equals(fm.getEsAlergica()) ? "SI" : "NO");
            dto.setDetalleAlergia(defaultString(fm.getDetalleAlergias()));
            dto.setTomaMed(Boolean.TRUE.equals(fm.getTomaMedicamentos()) ? "SI" : "NO");
            dto.setDetalleMed(defaultString(fm.getDetalleMedicamentos()));
            dto.setCondicionMed(defaultString(fm.getCondicionMedicaAdicional()));
            dto.setEnfermedad(defaultString(fm.getEnfermedad()));
            dto.setEnfermedadCual(defaultString(fm.getEnfermedadCual()));
            dto.setTratamiento(defaultString(fm.getTratamiento()));
            dto.setContramedicacion(defaultString(fm.getContramedicacion()));
            dto.setPrevsion(defaultString(fm.getPrevsion()));
            dto.setCesfam(defaultString(fm.getCesfam()));
            dto.setPersonaEmergencia(defaultString(fm.getPersonaEmergencia()));
            dto.setTelEmergencia(defaultString(fm.getTelEmergencia()));
            dto.setParentescoEmergencia(defaultString(fm.getParentescoEmergencia()));
        } else {
            dto.setEsAlergica("NO");
            dto.setTomaMed("NO");
        }

        return dto;
    }

    private String calcularCursoSiguiente(String cursoActual) {
        if (cursoActual == null || cursoActual.isBlank())
            return "";

        String curso = cursoActual.trim();
        String cursoUpper = curso.toUpperCase();

        if (cursoUpper.contains("PRE-KÍNDER") || cursoUpper.contains("PREKINDER")
                || cursoUpper.contains("PRE-KINDER")) {
            return curso.replaceAll("(?i)PRE-KÍNDER|PREKINDER|PRE-KINDER", "Kínder");
        }

        if (cursoUpper.contains("KÍNDER") || cursoUpper.contains("KINDER")) {
            return curso.replaceAll("(?i)KÍNDER|KINDER", "1° Básico");
        }

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)°");
        java.util.regex.Matcher matcher = pattern.matcher(curso);

        if (matcher.find()) {
            int gradoActual = Integer.parseInt(matcher.group(1));
            return matcher.replaceFirst((gradoActual + 1) + "°");
        }

        return curso;
    }

    private String defaultString(String str) {
        return (str != null && !str.isBlank() && !str.equalsIgnoreCase("null")) ? str.trim().toUpperCase() : "";
    }

    private Integer parseIntegerSafe(String val) {
        if (val == null || val.isBlank())
            return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Transactional
    public Matricula actualizarMatricula(Long id, MatriculaRequestDTO dto) {
        Matricula m = obtenerPorId(id);

        if (dto.getCursoActual() != null) {
            m.setCursoActual(dto.getCursoActual());
        }
        m.setCursoPostulacion(calcularCursoSiguiente(dto.getCursoActual()));
        m.setCorreoComprobante(dto.getCorreoComprobante());
        m.setRetSola(dto.getRetSola());
        m.setCondCont(dto.getCondContratado());
        m.setCondRun(dto.getCondRun());
        m.setCondTel(dto.getCondTel());

        if (dto.getAlumna() != null) {
            if (m.getAlumna() == null)
                m.setAlumna(new Alumna());
            var a = m.getAlumna();
            var aDto = dto.getAlumna();
            a.setNombres(aDto.getNombres());
            a.setApellidoPaterno(aDto.getApellidoPaterno());
            a.setApellidoMaterno(aDto.getApellidoMaterno());
            a.setRut(aDto.getRut());
            a.setFechaNacimiento(aDto.getFechaNacimiento());
            a.setDireccion(aDto.getDireccion());
            a.setComuna(aDto.getComuna());
            a.setViveCon(aDto.getViveCon());
            a.setAlumnaVilla(aDto.getAlumnaVilla());
            a.setNacionalidad(aDto.getAlumnaNacionalidad());
            a.setEtniaSiNo(aDto.getEtniaSiNo() != null ? (aDto.getEtniaSiNo() ? "SI" : "NO") : "NO");
            a.setEtnia(aDto.getEtnia());
            a.setProcedencia(aDto.getProcedencia());
            a.setNivelesRepetidos(aDto.getNivelesRepetidos());
            a.setNecesidadesEspeciales(aDto.getNecesidadesEspeciales());
        }

        if (dto.getApoderadoTitular() != null) {
            if (m.getApoderadoTitular() == null)
                m.setApoderadoTitular(new Familiar());
            actualizarCamposFamiliar(m.getApoderadoTitular(), dto.getApoderadoTitular());
        }

        if (dto.getApoderadoSuplente() != null) {
            if (m.getApoderadoSuplente() == null)
                m.setApoderadoSuplente(new Familiar());
            actualizarCamposFamiliar(m.getApoderadoSuplente(), dto.getApoderadoSuplente());
        }

        if (dto.getMadre() != null) {
            if (m.getMadre() == null)
                m.setMadre(new Familiar());
            actualizarCamposFamiliar(m.getMadre(), dto.getMadre());
        }

        if (dto.getPadre() != null) {
            if (m.getPadre() == null)
                m.setPadre(new Familiar());
            actualizarCamposFamiliar(m.getPadre(), dto.getPadre());
        }

        if (dto.getAutorizado1() != null) {
            if (m.getAutorizado1() == null)
                m.setAutorizado1(new AutorizadoRetiro());
            actualizarCamposAutorizado(m.getAutorizado1(), dto.getAutorizado1());
        }
        if (dto.getAutorizado2() != null) {
            if (m.getAutorizado2() == null)
                m.setAutorizado2(new AutorizadoRetiro());
            actualizarCamposAutorizado(m.getAutorizado2(), dto.getAutorizado2());
        }
        if (dto.getAutorizado3() != null) {
            if (m.getAutorizado3() == null)
                m.setAutorizado3(new AutorizadoRetiro());
            actualizarCamposAutorizado(m.getAutorizado3(), dto.getAutorizado3());
        }

        if (dto.getDatosFamiliares() != null) {
            if (m.getDatosFamiliares() == null)
                m.setDatosFamiliares(new DatosFamiliares());
            var df = m.getDatosFamiliares();
            var dfDto = dto.getDatosFamiliares();
            df.setNIntegrantes(parseIntegerSafe(dfDto.getNIntegrantes()));
            df.setNHermanos(parseIntegerSafe(dfDto.getNHermanos()));
            df.setTieneHermano(dfDto.getTieneHermano());
            df.setNomHer1(dfDto.getNomHer1());
            df.setNomHer2(dfDto.getNomHer2());
            df.setCursoHer1(dfDto.getCursoHer1());
            df.setCursoHer2(dfDto.getCursoHer2());
            df.setRedApoyo(dfDto.getRedApoyo());
            df.setPropia(dfDto.getPropia());
            df.setArrendada(dfDto.getArrendada());
            df.setCedida(dfDto.getCedida());
            df.setAllegado(dfDto.getAllegado());
        }

        if (dto.getFichaMedica() != null) {
            if (m.getFichaMedica() == null)
                m.setFichaMedica(new FichaMedica());
            var fm = m.getFichaMedica();
            var fmDto = dto.getFichaMedica();
            fm.setEsAlergica(fmDto.getEsAlergica());
            fm.setDetalleAlergias(fmDto.getDetalleAlergias());
            fm.setTomaMedicamentos(fmDto.getTomaMedicamentos());
            fm.setDetalleMedicamentos(fmDto.getDetalleMedicamentos());
            fm.setCondicionMedicaAdicional(fmDto.getCondicionMedicaAdicional());
            fm.setEnfermedad(fmDto.getEnfermedad());
            fm.setEnfermedadCual(fmDto.getEnfermedadCual());
            fm.setTratamiento(fmDto.getTratamiento());
            fm.setContramedicacion(fmDto.getContramedicacion());
            fm.setPrevsion(fmDto.getPrevsion());
            fm.setCesfam(fmDto.getCesfam());
            fm.setPersonaEmergencia(fmDto.getPersonaEmergencia());
            fm.setTelEmergencia(fmDto.getTelEmergencia());
            fm.setParentescoEmergencia(fmDto.getParentescoEmergencia());
        }

        return matriculaRepository.save(m);
    }

    private void actualizarCamposFamiliar(Familiar fam, FamiliarDTO dto) {
        fam.setNombres(dto.getNombres());
        fam.setApellidos(dto.getApellidos());
        fam.setRut(dto.getRut());
        fam.setParentesco(dto.getParentesco());
        fam.setTelefono(dto.getTelefono());
        fam.setCorreo(dto.getCorreo());
        fam.setDireccion(dto.getDireccion());
        fam.setComuna(dto.getComuna());
        fam.setEdad(dto.getEdad());
        fam.setApoFechaNac(dto.getApoFechaNac());
        fam.setApoGenero(dto.getGeneroApo());
        fam.setApoVilla(dto.getApoVilla());
        fam.setNivEstudioApo(dto.getNivEstudioApo());
        fam.setOcupacionApo(dto.getOcupacionApo());
        fam.setJornadaApo(dto.getJornadaApo());
        fam.setTelLabApo(dto.getTelLabApo());
        fam.setDirecLabApo(dto.getDirecLabApo());
        fam.setApoEstCivil(dto.getApoEstCivil());
    }

    private void actualizarCamposAutorizado(AutorizadoRetiro aut, MatriculaRequestDTO.AutorizadoRetiroDTO dto) {
        aut.setRut(dto.getRut());
        aut.setNombreCompleto(dto.getNombreCompleto());
        aut.setParentescoFurgon(dto.getParentesco());
        aut.setTelefono(dto.getTelefono());
    }

    @Transactional(readOnly = true)
    public byte[] generarFichaPdf(Matricula m) {
        String rutaPlantilla = "templates/plantilla_ficha.docx";

        try (InputStream in = new ClassPathResource(rutaPlantilla).getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IXDocReport report = XDocReportRegistry.getRegistry()
                    .loadReport(in, TemplateEngineKind.Velocity);

            FichaDto dto = mapear(m);
            IContext ctx = report.createContext();

            // 1. Datos generales
            ctx.put("idMatricula", dto.getNumeroMatricula());
            ctx.put("curso2027", dto.getCurso2027());
            ctx.put("curso", dto.getCurso());

            // 2. Alumna
            ctx.put("nombreAlumna", dto.getNombreAlumna());
            ctx.put("apellidoPAlumna", dto.getApellidoPAlumna());
            ctx.put("apellidoMAlumna", dto.getApellidoMAlumna());
            ctx.put("rutAlumno", dto.getRutAlumna());
            ctx.put("alumnoFechaN", dto.getAlumnoFechaN());
            ctx.put("alumnaDireccion", dto.getAlumnaDireccion());
            ctx.put("aluComuna", dto.getAluComuna());
            ctx.put("viveCon", dto.getViveCon());
            ctx.put("aluNomCom", dto.getAluNomCom());
            ctx.put("alumnaVilla", dto.getAlumnaVilla());
            ctx.put("alumnaNacionalidad", dto.getAlumnaNacionalidad());
            ctx.put("etniaSiNo", dto.getEtniaSiNo());
            ctx.put("etnia", dto.getEtnia());
            ctx.put("procedencia", dto.getProcedencia());
            ctx.put("nivelesRepetidos", dto.getNivelesRepetidos());

            // 3. Apoderados
            ctx.put("apoNombre", dto.getApoNombre());
            ctx.put("apoApellidos", dto.getApoApellidos());
            ctx.put("apoRut", dto.getApoRut());
            ctx.put("apoParentesco", dto.getApoParentesco());
            ctx.put("apoTelefono", dto.getApoTelefono());
            ctx.put("apoCorreo", dto.getApoCorreo());
            ctx.put("apoDireccion", dto.getApoDireccion());
            ctx.put("apoComuna", dto.getApoComuna());
            ctx.put("apoNomCom", dto.getApoNomCom());
            ctx.put("edadApo", dto.getEdadApo());
            ctx.put("apoFechaNac", dto.getApoFechaNac());
            ctx.put("generoApo", dto.getGeneroApo());
            ctx.put("apoVilla", dto.getApoVilla());
            ctx.put("nivEstudioApo", dto.getNivEstudioApo());
            ctx.put("ocupacionApo", dto.getOcupacionApo());
            ctx.put("jornadaApo", dto.getJornadaApo());
            ctx.put("telLabApo", dto.getTelLabApo());
            ctx.put("direcLabApo", dto.getDirecLabApo());
            ctx.put("apoEstCivil", dto.getApoEstCivil());

            ctx.put("apo2Nombre", dto.getApo2Nombre());
            ctx.put("apo2Apellidos", dto.getApo2Apellidos());
            ctx.put("apo2Rut", dto.getApo2Rut());
            ctx.put("apo2Parentesco", dto.getApo2Parentesco());
            ctx.put("apo2Telefono", dto.getApo2Telefono());
            ctx.put("apo2Correo", dto.getApo2Correo());
            ctx.put("apo2Dire", dto.getApo2Dire());
            ctx.put("apo2Comuna", dto.getApo2Comuna());
            ctx.put("edadApo2", dto.getEdadApo2());
            ctx.put("fechaNacApo2", dto.getFechaNacApo2());
            ctx.put("generoApo2", dto.getGeneroApo2());
            ctx.put("apo2EstCivil", dto.getApo2EstCivil());
            ctx.put("apo2NivEst", dto.getApo2NivEst());
            ctx.put("apo2Ocupacion", dto.getApo2Ocupacion());
            ctx.put("apo2Jornada", dto.getApo2Jornada());
            ctx.put("apo2TelLab", dto.getApo2TelLab());
            ctx.put("apo2DirecLab", dto.getApo2DirecLab());
            ctx.put("apo2Villa", dto.getApo2Villa());

            // 4. Retiro
            ctx.put("nombreRet1", dto.getNombreRet1());
            ctx.put("rutRet1", dto.getRutRetira1());
            ctx.put("parenRet1", dto.getParenRet1());
            ctx.put("telRet1", dto.getTelRet1());

            ctx.put("nombreRet2", dto.getNombreRet2());
            ctx.put("rutRet2", dto.getRutRetira2());
            ctx.put("parenRet2", dto.getParenRet2());
            ctx.put("telRet2", dto.getTelRet2());

            ctx.put("nombreRet3", dto.getNombreRet3());
            ctx.put("rutRet3", dto.getRutRetira3());
            ctx.put("parenRet3", dto.getParenRet3());
            ctx.put("telRet3", dto.getTelRet3());

            ctx.put("retSola", dto.getRetSola());
            ctx.put("condCont", dto.getCondCont());
            ctx.put("condRun", dto.getCondRun());
            ctx.put("condTel", dto.getCondTel());

            // 5. Datos Familiares
            ctx.put("nIntegrantes", dto.getNIntegrantes());
            ctx.put("nHermanos", dto.getNHermanos());
            ctx.put("tieneHermano", dto.getTieneHermano());
            ctx.put("nomHer1", dto.getNomHer1());
            ctx.put("nomHer2", dto.getNomHer2());
            ctx.put("cursoHer1", dto.getCursoHer1());
            ctx.put("cursoHer2", dto.getCursoHer2());
            ctx.put("redApoyo", dto.getRedApoyo());
            ctx.put("propia", dto.getPropia());
            ctx.put("arrendada", dto.getArrendada());
            ctx.put("cedida", dto.getCedida());
            ctx.put("allegado", dto.getAllegado());

            // 6. Salud
            ctx.put("esAlergica", dto.getEsAlergica());
            ctx.put("detalleAlergia", dto.getDetalleAlergia());
            ctx.put("tomaMed", dto.getTomaMed());
            ctx.put("detalleMed", dto.getDetalleMed());
            ctx.put("condicionMed", dto.getCondicionMed());
            ctx.put("enfermedad", dto.getEnfermedad());
            ctx.put("enfermedadCual", dto.getEnfermedadCual());
            ctx.put("tratamiento", dto.getTratamiento());
            ctx.put("contramedicacion", dto.getContramedicacion());
            ctx.put("prevsion", dto.getPrevsion());
            ctx.put("cesfam", dto.getCesfam());
            ctx.put("personaEmergencia", dto.getPersonaEmergencia());
            ctx.put("telEmergencia", dto.getTelEmergencia());
            ctx.put("parentescoEmergencia", dto.getParentescoEmergencia());

            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

            report.convert(ctx, options, out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al convertir la ficha a PDF", e);
        }
    }
}