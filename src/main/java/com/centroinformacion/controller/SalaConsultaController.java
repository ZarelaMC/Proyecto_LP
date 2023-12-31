package com.centroinformacion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.centroinformacion.entity.Sala;
import com.centroinformacion.service.SalaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@CommonsLog
@Controller
public class SalaConsultaController {
	@Autowired
	private SalaService salaService;

	@ResponseBody
	@GetMapping("/consultaSala")
	public List<Sala> consulta(int estado, String numero, int piso, int numAlumnos, String recursos, int idSede,
			int idTipoSala) {
		List<Sala> lstSalida = salaService.listaConsultaSala(estado, "%" + numero +"%", piso, numAlumnos, "%"+recursos+"%", idSede, idTipoSala);
		return lstSalida;
	}

	@GetMapping("/reporteSalaPdf")
	public void reportes(HttpServletRequest request, HttpServletResponse response, boolean paramEstado,
			String paramNumero, String paramPiso, String paramnumAlumnos, String paramRecursos, int paramsede, int paramtipoSala) {

		try {
			// PASO 1: obtener el data surce que va a generar el reporte
			List<Sala> lstSalida = salaService.listaConsultaSala(paramEstado ?1:0, "%" + paramNumero +"%", paramPiso =="" ?-1 : Integer.parseInt(paramPiso), paramnumAlumnos  =="" ?-1 : Integer.parseInt(paramnumAlumnos), "%"+paramRecursos+"%", paramsede,paramtipoSala );

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lstSalida);

			// PASO 2: Obtener el archivo que contiene el diseño del reporte
			String fileDirectory = request.getServletContext().getRealPath("/WEB-INF/reportes/reporteSala.jasper");
			log.info(">>> File Reporte >> " + fileDirectory);

			FileInputStream stream = new FileInputStream(new File(fileDirectory));

			// PASO 3: Parametros adicionales
			String fileLogo = request.getServletContext().getRealPath("/WEB-INF/img/cherry.jpg");
			log.info(">>> File Logo >> " + fileLogo);

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("RUTA_LOGO", fileLogo);

			// PASO 4: Enviamos dataSource, diseño y parámetros para generar el PDF
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

			// PASO 5: Enviar el PDF generado
			response.setContentType("application/x-pdf");
			response.addHeader("Content-disposition", "attachment; filename=ReporteSala.pdf");

			OutputStream outStream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}