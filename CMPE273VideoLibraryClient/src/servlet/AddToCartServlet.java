package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.MovieCart;
import model.User;

/**
 * Servlet implementation class Login
 */
public class AddToCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	services.ServiceProxy proxy = new services.ServiceProxy();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddToCartServlet() {
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
		try {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			String[] selectedMovies = request.getParameterValues("checkboxId");
			System.out.println("Movies selected size:: "
					+ selectedMovies.length);
			proxy.setEndpoint("http://localhost:8080/CMPE273VideoLibrary/services/Service");
			System.out.println("Going to add the movies: "+selectedMovies.toString());
			MovieCart[] movieCartArray = new MovieCart[selectedMovies.length];
			MovieCart[] returnMovieCartArray = new MovieCart[selectedMovies.length];
			for(int i=0; i<movieCartArray.length;i++){
				MovieCart movieCart = new MovieCart();
				movieCart.setMovieId(Integer.parseInt(selectedMovies[i]));
				movieCart.setUserId(user.getId());
				//System.out.println("Cart item "+i+" Movie: "+mov);
				movieCartArray[i] = movieCart;
			}
			returnMovieCartArray = proxy.addToCart(movieCartArray);
			response.sendRedirect("AddedToCart.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
