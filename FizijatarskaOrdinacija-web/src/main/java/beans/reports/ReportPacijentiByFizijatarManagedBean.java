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

import reportModel.PacijentFizijatarModel;
import managers.PacijentManager;
import model.Pacijent;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean
@SessionScoped
public class ReportPacijentiByFizijatarManagedBean implements Serializable 
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

	public ReportPacijentiByFizijatarManagedBean() {
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

			if (reportType.equals("allPacijentiByFizijatar")) {

				List<Pacijent> cleanData = PacijentManager.getAllPacijent();
				List<PacijentFizijatarModel> data = PacijentFizijatarModel
						.convertPacijent(cleanData);

				jasperFile = reportsDirectory
						+ "allPacijentiByFizijatar.jasper";
				if (data.size() == 0) {
					jasperPrint = JasperFillManager.fillReport(jasperFile,
							params, emptyDataSource);
				} else {
					dataSource = new JRBeanCollectionDataSource(data, false);
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
