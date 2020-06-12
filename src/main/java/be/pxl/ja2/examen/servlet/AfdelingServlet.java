package be.pxl.ja2.examen.servlet;

import be.pxl.ja2.examen.model.Bezoeker;
import be.pxl.ja2.examen.service.BezoekersService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/afdeling")
public class AfdelingServlet extends HttpServlet {

	@Autowired
	private BezoekersService bezoekersService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		String afdelingCode = req.getParameter("code");
		List<Bezoeker> bezoekersVoorAfdeling = bezoekersService.getBezoekersVoorAfdeling(afdelingCode).stream().sorted(Comparator.comparing(Bezoeker::getTijdstip)).collect(Collectors.toList());
		try (PrintWriter out = resp.getWriter()) {
			out.println("<html>");
			out.println("<body>");
			for (Bezoeker bezoeker : bezoekersVoorAfdeling) {
				out.println("<div>");
				out.println("Tijdstip: " + bezoeker.getTijdstip());
				out.println(" Patient: " + bezoeker.getPatient().getCode());
				out.println(" Bezoeker: " + " " + bezoeker.getInfo() + "<div>");
			}
			out.println("</body>");
			out.println("</html>");
		}
	}
}

