package com.centroinformacion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.centroinformacion.entity.Revista;
import com.centroinformacion.service.RevistaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
@CommonsLog
public class RevistaConsultaController {

	@Autowired
	private RevistaService revistaService;
	
	@GetMapping("/consultarRevista")
	@ResponseBody
	public List<Revista> consulta (int estado, 
									String nombre,
									String frecuencia,
									int idPais, 
									int idDataCatalogo,
									@DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
									@DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta){
		
		List<Revista> lstSalida = revistaService.listaConsultaRevista(estado,idPais,idDataCatalogo,"%"+nombre +"%","%"+frecuencia+"%", desde, hasta);
		
		return lstSalida;
	}
	
	@GetMapping("/reporteEmpleadoPdf")
	public void reportes(HttpServletRequest request,
						 HttpServletResponse response,
						 boolean paramEstado, 
						 int paramPais, 
						 int paramTipoRevista,
						 String paramNombre, 
						 String paramFrecuencia,
						 @DateTimeFormat(pattern = "yyyy-MM-dd") Date paramFechaCreacionDesde,
						 @DateTimeFormat(pattern = "yyyy-MM-dd") Date paramFechaCreacionHasta) {
		
		try {
			
		
		//PASO 1: Obtener el dataSource que va generar el reporte
		List<Revista> lstSalida = revistaService.listaConsultaRevista(
						paramEstado ?1:0, 
						paramPais,
						paramTipoRevista,
						"%"+paramFrecuencia+"%",
						"%"+paramNombre+"%", 
						paramFechaCreacionDesde, 
						paramFechaCreacionHasta);
		
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lstSalida);
		
		//PASO 2: Obtener el archivo que contiene el diseño del reporte
		String fileDirectory = request.getServletContext().getRealPath("/WEB-INF/reportes/reporteRevista.jasper"); 
		log.info(">>> File Reporte >> " + fileDirectory);
		FileInputStream stream = new  FileInputStream(new File(fileDirectory));
		
		//PASO 3: Parametros adicionales
		String fileLogo = request.getServletContext().getRealPath("/WEB-INF/img/descarga.jpg");
		log.info(">>> File Logo >> " + fileLogo);
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("RUTA_LOGO", fileLogo);
		
		//PASO 4: Enviamos dataSource, diseño y parámetros para generar el PDF
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
	    
	    //PASO 5: Enviar el PDF generado
	  	response.setContentType("application/x-pdf");
	  	response.addHeader("Content-disposition", "attachment; filename=ReporteRevista.pdf");

	  	OutputStream outStream = response.getOutputStream();
	  	JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	  		
	    
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
