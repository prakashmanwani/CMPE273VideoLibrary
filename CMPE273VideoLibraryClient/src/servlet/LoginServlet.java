package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;

/**
 * Servlet implementation class Login
 */
// @WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			User user = new User();
			services.ServiceProxy proxy = new services.ServiceProxy();
			proxy.setEndpoint("http://localhost:8080/CMPE273VideoLibrary/services/Service");
			System.out.println("Login :::::::: Form Submitted");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			// System.out.println("Username :::::::" + username);
			// System.out.println("Password :::::::" + password);
			user = proxy.signIn(username, password);
			if (user.getErrorMsg().equals("SUCCESS")) {
				if ((user.getPreference().getPreferenceId() == 1)
						|| (user.getPreference().getPreferenceId() == 2)
						|| (user.getPreference().getPreferenceId() == 3)) {
					HttpSession session = request.getSession();
					session.setAttribute("user", user);
					response.sendRedirect("Home.jsp");
				}
			} else {

				request.setAttribute("errorMsg", user.getErrorMsg());
				System.out.println("error Msg in servlet:"
						+ request.getAttribute("errorMsg"));
				// response.sendRedirect("SignIn.jsp");
				RequestDispatcher requestDispatcher = request
						.getRequestDispatcher("Login.jsp");
				requestDispatcher.forward(request, response);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
