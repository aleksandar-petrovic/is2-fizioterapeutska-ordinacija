package beans.reports;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import reportModel.FizijatarModel;
import managers.FizijatarManager;
import model.Fizijatar;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean
@SessionScoped
public class ReportFizijatriManagedBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object> params;
	private JREmptyDataSource emptyDataSource = new JREmptyDataSource();
	private JRDataSource dataSource = null;
	private ServletContext context;
	private JasperPrint jasperPrint = null;
	private String reportsDirectory;
	private String jasperFile = null;
	private HttpServletResponse response;

	public ReportFizijatriManagedBean() {
		params = new HashMap<String, Object>();
	}

	public void generateReport(String reportType) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			ExternalContext externalContext = facesContext.getExternalContext();
			context = (ServletContext) externalContext.getContext();
			reportsDirectory = context.getRealPath("/")
					+ "/WEB-INF/classes/jasper/";
			response = (HttpServletResponse) externalContext.getResponse();

			if (reportType.equals("allFizijatri")) {

				List<Fizijatar> cleanData = FizijatarManager.getAllFizijatar();
				List<FizijatarModel> data = FizijatarModel
						.convertListOfFizijatarToFizijatarModel(cleanData);

				jasperFile = reportsDirectory + "allFizijatri.jasper";
				if (data.size() == 0) {
					jasperPrint = JasperFillManager.fillReport(jasperFile,
							params, emptyDataSource);
				} else {
					dataSource = new JRBeanCollectionDataSource(data, false); // important,
																				// solve
																				// error
					jasperPrint = JasperFillManager.fillReport(jasperFile,
							params, dataSource);
				}
			}

			ServletOutputStream servletOutputStream = response
					.getOutputStream();
			response.setContentType("application/pdf");
			JasperExportManager.exportReportToPdfStream(jasperPrint,
					servletOutputStream);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			facesContext.responseComplete();
		}
	}

}
