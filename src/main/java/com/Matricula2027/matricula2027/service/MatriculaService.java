package com.Matricula2027.matricula2027.service;

import com.Matricula2027.matricula2027.dto.FichaDto;
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
        String rutAlumna = dto.getAlumna().getRut();
        if (alumnaRepository.existsByRut(rutAlumna)) {
            throw new IllegalArgumentException("Ya existe una alumna registrada con el RUT: " + rutAlumna);
        }

        Familiar apoderado = obtenerOCrearFamiliar(dto.getApoderadoTitular());
        Familiar apoderadoSuplente = obtenerOCrearFamiliar(dto.getApoderadoSuplente());
        Familiar madre = obtenerOCrearFamiliar(dto.getMadre());
        Familiar padre = obtenerOCrearFamiliar(dto.getPadre());

        Alumna alumna = new Alumna();
        alumna.setRut(dto.getAlumna().getRut());
        alumna.setNombres(dto.getAlumna().getNombres());
        alumna.setApellidoPaterno(dto.getAlumna().getApellidoPaterno());
        alumna.setApellidoMaterno(dto.getAlumna().getApellidoMaterno());
        alumna.setFechaNacimiento(dto.getAlumna().getFechaNacimiento());
        alumna.setDireccion(dto.getAlumna().getDireccion());
        alumna.setComuna(dto.getAlumna().getComuna());
        alumna.setViveCon(dto.getAlumna().getViveCon());

        Matricula matricula = new Matricula();
        matricula.setCursoActual(dto.getCursoActual());
        matricula.setApoderadoTitular(apoderado);
        matricula.setApoderadoSuplente(apoderadoSuplente);
        matricula.setMadre(madre);
        matricula.setPadre(padre);
        matricula.setFechaHoraRegistro(LocalDateTime.now());
        matricula.setNumeroMatricula(generarNumeroMatricula());
        matricula.setEstadoFicha("PENDIENTE");
        matricula.setCorreoComprobante(dto.getApoderadoTitular() != null && dto.getApoderadoTitular().getCorreo() != null
                ? dto.getApoderadoTitular().getCorreo()
                : dto.getCorreoComprobante());
        matricula.setCursoPostulacion(calcularCursoSiguiente(dto.getCursoActual()));
        matricula.setAlumna(alumna);

        if (dto.getAutorizadoRetiro() != null) {
            AutorizadoRetiro autorizado = new AutorizadoRetiro();
            autorizado.setRut(dto.getAutorizadoRetiro().getRut());
            autorizado.setNombreCompleto(dto.getAutorizadoRetiro().getNombreCompleto());
            autorizado.setParentescoFurgon(dto.getAutorizadoRetiro().getParentesco());
            autorizado.setTelefono(dto.getAutorizadoRetiro().getTelefono());
            autorizado.setMatricula(matricula);
            matricula.setAutorizadosRetiro(autorizado);
        }

        if (dto.getFichaMedica() != null) {
            FichaMedica fichaMedica = new FichaMedica();
            fichaMedica.setEsAlergica(dto.getFichaMedica().getEsAlergica());
            fichaMedica.setDetalleAlergias(dto.getFichaMedica().getDetalleAlergias());
            fichaMedica.setTomaMedicamentos(dto.getFichaMedica().getTomaMedicamentos());
            fichaMedica.setDetalleMedicamentos(dto.getFichaMedica().getDetalleMedicamentos());
            fichaMedica.setCondicionMedicaAdicional(dto.getFichaMedica().getCondicionMedicaAdicional());
            matricula.setFichaMedica(fichaMedica);
        }

        return matriculaRepository.saveAndFlush(matricula);
    }

    private Familiar obtenerOCrearFamiliar(FamiliarDTO dto) {
        if (dto == null || dto.getRut() == null || dto.getRut().isBlank()) {
            return null;
        }

        return familiarRepository.findByRut(dto.getRut())
                .map(familiarExistente -> {
                    if (dto.getNombres() != null) familiarExistente.setNombres(dto.getNombres());
                    if (dto.getApellidos() != null) familiarExistente.setApellidos(dto.getApellidos());
                    if (dto.getParentesco() != null) familiarExistente.setParentesco(dto.getParentesco());
                    if (dto.getDireccion() != null) familiarExistente.setDireccion(dto.getDireccion());
                    if (dto.getComuna() != null) familiarExistente.setComuna(dto.getComuna());
                    if (dto.getTelefono() != null) familiarExistente.setTelefono(dto.getTelefono());
                    if (dto.getCorreo() != null) familiarExistente.setCorreo(dto.getCorreo());
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
                    return familiarRepository.save(nuevo);
                });
    }

    private String generarNumeroMatricula() {
        long cantidadActual = matriculaRepository.count();
        return String.format("2027-MAT-%04d", cantidadActual + 1);
    }

    @Transactional(readOnly = true)
    public List<Matricula> obtenerTodas() {
        return matriculaRepository.findAll();
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
    public Matricula actualizarEstado(Long id, String nuevoEstado) {
        Matricula matricula = obtenerPorId(id);
        matricula.setEstadoFicha(nuevoEstado);
        return matriculaRepository.save(matricula);
    }

    // --- GENERACIÓN DE FICHA DOCX MEDIANTE XDOCREPORT ---
    @Transactional(readOnly = true)
    public byte[] generarFichaDocx(Matricula m) {
        String rutaPlantilla = "templates/plantilla_ficha.docx"; // Ajustar si está en la raíz de resources
        
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

            // 4. Apoderado Suplente
            ctx.put("apo2Nombre", dto.getApo2Nombre());
            ctx.put("apo2Apellidos", dto.getApo2Apellidos());
            ctx.put("apo2Rut", dto.getApo2Rut());
            ctx.put("apo2Parentesco", dto.getApo2Parentesco());
            ctx.put("apo2Telefono", dto.getApo2Telefono());
            ctx.put("apo2Correo", dto.getApo2Correo());
            ctx.put("apo2Dire", dto.getApo2Dire());
            ctx.put("apo2Comuna", dto.getApo2Comuna());

            // 5. Madre
            ctx.put("madreNombres", dto.getMadreNombres());
            ctx.put("madreApellidos", dto.getMadreApellidos());
            ctx.put("madreRut", dto.getMadreRut());
            ctx.put("madreTel", dto.getMadreTel());
            ctx.put("madreDir", dto.getMadreDir());
            ctx.put("madreCom", dto.getMadreCom());

            // 6. Padre
            ctx.put("padreNombres", dto.getPadreNombres());
            ctx.put("padreApellidos", dto.getPadreApellidos());
            ctx.put("padreRut", dto.getPadreRut());
            ctx.put("padreTel", dto.getPadreTel());
            ctx.put("padreDir", dto.getPadreDir());
            ctx.put("padreCom", dto.getPadreCom());

            // 7. Retiro / Salud
            ctx.put("nombreRet", dto.getNombreRet());
            ctx.put("rutRetira", dto.getRutRetira());
            ctx.put("parenRet", dto.getParenRet());
            ctx.put("telRet", dto.getTelRet());
            ctx.put("esAlergica", dto.getEsAlergica());
            ctx.put("detalleAlergia", dto.getDetalleAlergia());
            ctx.put("tomaMed", dto.getTomaMed());
            ctx.put("detalleMed", dto.getDetalleMed());
            ctx.put("condicionMed", dto.getCondicionMed());

            report.process(ctx, out);
            return out.toByteArray();

        } catch (Exception e) {
            System.err.println("Error generando ficha DOCX con XDocReport: " + e.getMessage());
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

            String apoNomCom = String.format("%s %s", defaultString(apo.getNombres()), defaultString(apo.getApellidos())).trim();
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
        }

        // 5. Madre
        Familiar madre = m.getMadre();
        if (madre != null) {
            dto.setMadreNombres(defaultString(madre.getNombres()));
            dto.setMadreApellidos(defaultString(madre.getApellidos()));
            dto.setMadreRut(defaultString(madre.getRut()));
            dto.setMadreTel(defaultString(madre.getTelefono()));
            dto.setMadreDir(defaultString(madre.getDireccion()));
            dto.setMadreCom(defaultString(madre.getComuna()));
        }

        // 6. Padre
        Familiar padre = m.getPadre();
        if (padre != null) {
            dto.setPadreNombres(defaultString(padre.getNombres()));
            dto.setPadreApellidos(defaultString(padre.getApellidos()));
            dto.setPadreRut(defaultString(padre.getRut()));
            dto.setPadreTel(defaultString(padre.getTelefono()));
            dto.setPadreDir(defaultString(padre.getDireccion()));
            dto.setPadreCom(defaultString(padre.getComuna()));
        }

        // 7. Retiro
        AutorizadoRetiro ret = m.getAutorizadosRetiro();
        if (ret != null) {
            dto.setNombreRet(defaultString(ret.getNombreCompleto()));
            dto.setRutRetira(defaultString(ret.getRut()));
            dto.setParenRet(defaultString(ret.getParentescoFurgon()));
            dto.setTelRet(defaultString(ret.getTelefono()));
        }

        // 8. Ficha Médica
        FichaMedica fm = m.getFichaMedica();
        if (fm != null) {
            dto.setEsAlergica(Boolean.TRUE.equals(fm.getEsAlergica()) ? "SI" : "NO");
            dto.setDetalleAlergia(defaultString(fm.getDetalleAlergias()));
            dto.setTomaMed(Boolean.TRUE.equals(fm.getTomaMedicamentos()) ? "SI" : "NO");
            dto.setDetalleMed(defaultString(fm.getDetalleMedicamentos()));
            dto.setCondicionMed(defaultString(fm.getCondicionMedicaAdicional()));
        } else {
            dto.setEsAlergica("NO");
            dto.setTomaMed("NO");
        }

        return dto;
    }

    private String calcularCursoSiguiente(String cursoActual) {
        if (cursoActual == null || cursoActual.isBlank()) return "";

        String curso = cursoActual.trim();
        String cursoUpper = curso.toUpperCase();

        if (cursoUpper.contains("PRE-KÍNDER") || cursoUpper.contains("PREKINDER") || cursoUpper.contains("PRE-KINDER")) {
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

    @Transactional
    public Matricula actualizarMatricula(Long id, MatriculaRequestDTO dto) {
        Matricula m = obtenerPorId(id);

        if (dto.getCursoActual() != null) {
            m.setCursoActual(dto.getCursoActual());
        }
        m.setCursoPostulacion(calcularCursoSiguiente(dto.getCursoActual()));
        m.setCorreoComprobante(dto.getCorreoComprobante());

        if (dto.getAlumna() != null) {
            if (m.getAlumna() == null) m.setAlumna(new Alumna());
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
        }

        if (dto.getApoderadoTitular() != null) {
            if (m.getApoderadoTitular() == null) m.setApoderadoTitular(new Familiar());
            var apo1 = m.getApoderadoTitular();
            var apo1Dto = dto.getApoderadoTitular();
            apo1.setNombres(apo1Dto.getNombres());
            apo1.setApellidos(apo1Dto.getApellidos());
            apo1.setRut(apo1Dto.getRut());
            apo1.setParentesco(apo1Dto.getParentesco());
            apo1.setTelefono(apo1Dto.getTelefono());
            apo1.setCorreo(apo1Dto.getCorreo());
            apo1.setDireccion(apo1Dto.getDireccion());
            apo1.setComuna(apo1Dto.getComuna());
        }

        if (dto.getApoderadoSuplente() != null) {
            if (m.getApoderadoSuplente() == null) m.setApoderadoSuplente(new Familiar());
            var apo2 = m.getApoderadoSuplente();
            var apo2Dto = dto.getApoderadoSuplente();
            apo2.setNombres(apo2Dto.getNombres());
            apo2.setApellidos(apo2Dto.getApellidos());
            apo2.setRut(apo2Dto.getRut());
            apo2.setParentesco(apo2Dto.getParentesco());
            apo2.setTelefono(apo2Dto.getTelefono());
            apo2.setCorreo(apo2Dto.getCorreo());
            apo2.setDireccion(apo2Dto.getDireccion());
            apo2.setComuna(apo2Dto.getComuna());
        }

        if (dto.getMadre() != null) {
            if (m.getMadre() == null) m.setMadre(new Familiar());
            var madre = m.getMadre();
            var madreDto = dto.getMadre();
            madre.setRut(madreDto.getRut());
            madre.setNombres(madreDto.getNombres());
            madre.setApellidos(madreDto.getApellidos());
            madre.setParentesco(madreDto.getParentesco() != null ? madreDto.getParentesco() : "MADRE");
            madre.setTelefono(madreDto.getTelefono());
            madre.setCorreo(madreDto.getCorreo());
            madre.setDireccion(madreDto.getDireccion());
            madre.setComuna(madreDto.getComuna());
        }

        if (dto.getPadre() != null) {
            if (m.getPadre() == null) m.setPadre(new Familiar());
            var padre = m.getPadre();
            var padreDto = dto.getPadre();
            padre.setRut(padreDto.getRut());
            padre.setNombres(padreDto.getNombres());
            padre.setApellidos(padreDto.getApellidos());
            padre.setParentesco(padreDto.getParentesco() != null ? padreDto.getParentesco() : "PADRE");
            padre.setTelefono(padreDto.getTelefono());
            padre.setCorreo(padreDto.getCorreo());
            padre.setDireccion(padreDto.getDireccion());
            padre.setComuna(padreDto.getComuna());
        }

        if (dto.getAutorizadoRetiro() != null) {
            if (m.getAutorizadosRetiro() == null) m.setAutorizadosRetiro(new AutorizadoRetiro());
            var ret = m.getAutorizadosRetiro();
            var retDto = dto.getAutorizadoRetiro();
            ret.setRut(retDto.getRut());
            ret.setNombreCompleto(retDto.getNombreCompleto());
            ret.setParentescoFurgon(retDto.getParentesco());
            ret.setTelefono(retDto.getTelefono());
        }

        if (dto.getFichaMedica() != null) {
            if (m.getFichaMedica() == null) m.setFichaMedica(new FichaMedica());
            var fm = m.getFichaMedica();
            var fmDto = dto.getFichaMedica();
            fm.setEsAlergica(fmDto.getEsAlergica());
            fm.setDetalleAlergias(fmDto.getDetalleAlergias());
            fm.setTomaMedicamentos(fmDto.getTomaMedicamentos());
            fm.setDetalleMedicamentos(fmDto.getDetalleMedicamentos());
            fm.setCondicionMedicaAdicional(fmDto.getCondicionMedicaAdicional());
        }

        return matriculaRepository.save(m);
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

        ctx.put("apo2Nombre", dto.getApo2Nombre());
        ctx.put("apo2Apellidos", dto.getApo2Apellidos());
        ctx.put("apo2Rut", dto.getApo2Rut());
        ctx.put("apo2Parentesco", dto.getApo2Parentesco());
        ctx.put("apo2Telefono", dto.getApo2Telefono());
        ctx.put("apo2Correo", dto.getApo2Correo());
        ctx.put("apo2Dire", dto.getApo2Dire());
        ctx.put("apo2Comuna", dto.getApo2Comuna());

        // 4. Padres
        ctx.put("madreNombres", dto.getMadreNombres());
        ctx.put("madreApellidos", dto.getMadreApellidos());
        ctx.put("madreRut", dto.getMadreRut());
        ctx.put("madreTel", dto.getMadreTel());
        ctx.put("madreDir", dto.getMadreDir());
        ctx.put("madreCom", dto.getMadreCom());

        ctx.put("padreNombres", dto.getPadreNombres());
        ctx.put("padreApellidos", dto.getPadreApellidos());
        ctx.put("padreRut", dto.getPadreRut());
        ctx.put("padreTel", dto.getPadreTel());
        ctx.put("padreDir", dto.getPadreDir());
        ctx.put("padreCom", dto.getPadreCom());

        // 5. Retiro y Salud
        ctx.put("nombreRet", dto.getNombreRet());
        ctx.put("rutRetira", dto.getRutRetira());
        ctx.put("parenRet", dto.getParenRet());
        ctx.put("telRet", dto.getTelRet());
        ctx.put("esAlergica", dto.getEsAlergica());
        ctx.put("detalleAlergia", dto.getDetalleAlergia());
        ctx.put("tomaMed", dto.getTomaMed());
        ctx.put("detalleMed", dto.getDetalleMed());
        ctx.put("condicionMed", dto.getCondicionMed());

        // Configurar conversión DOCX -> PDF
        Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

        report.convert(ctx, options, out);
        return out.toByteArray();

    } catch (Exception e) {
        throw new RuntimeException("Error al convertir la ficha a PDF", e);
    }
}
}