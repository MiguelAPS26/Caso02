
package com.lab6.veterinaria.controller;


import com.lab6.veterinaria.model.Mascota;
import com.lab6.veterinaria.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService service;

    // Listar todas las mascotas
    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", service.listar());
        return "lista_mascotas"; // Debe corresponder con el nombre del archivo HTML
    }

    // Mostrar formulario para nueva mascota
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "formulario_mascotas"; // Nombre del archivo HTML del formulario
    }

    // Guardar nueva mascota
    @PostMapping
    public String guardar(@ModelAttribute Mascota mascota) {
        service.guardar(mascota);
        return "redirect:/mascotas";
    }

    // Eliminar mascota por ID
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id) {
        service.eliminar(id);
        return "redirect:/mascotas";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") int id, Model model) {
        Mascota mascota = service.obtenerPorId(id);
        if (mascota != null) {
            model.addAttribute("mascota", mascota);
            return "formulario_mascotas";
        }
        return "redirect:/mascotas"; // Redirige si no encuentra la mascota
    }

    // Guardar cambios en mascota
    @PostMapping("/editar")
    public String guardarEdicion(@ModelAttribute Mascota mascota) {
        service.guardar(mascota);
        return "redirect:/mascotas";
    }

    // Exportar lista de mascotas a PDF
    @GetMapping("/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=mascotas.pdf");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            document.add(new Paragraph("Reporte de Mascotas").setBold().setFontSize(18));

            Table table = new Table(5); // 5 columnas
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Edad");
            table.addCell("Especie");
            table.addCell("Raza");

            for (Mascota m : service.listar()) {
                table.addCell(String.valueOf(m.getId()));
                table.addCell(m.getNombre());
                table.addCell(String.valueOf(m.getEdad()));
                table.addCell(m.getEspecie());
                table.addCell(m.getRaza());
            }
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Exportar lista de mascotas a Excel
    @GetMapping("/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=mascotas.xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             var outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Mascotas");

            // Cabeceras
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Edad");
            header.createCell(3).setCellValue("Especie");
            header.createCell(4).setCellValue("Raza");

            // Datos
            int rowIdx = 1;
            for (Mascota m : service.listar()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getId());
                row.createCell(1).setCellValue(m.getNombre());
                row.createCell(2).setCellValue(m.getEdad());
                row.createCell(3).setCellValue(m.getEspecie());
                row.createCell(4).setCellValue(m.getRaza());
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 