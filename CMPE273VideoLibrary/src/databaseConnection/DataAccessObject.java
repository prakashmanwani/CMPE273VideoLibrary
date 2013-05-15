package databaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Address;
import model.City;
import model.Country;
import model.Movie;
import model.MovieCart;
import model.MovieCategory;
import model.Preference;
import model.State;
import model.User;

public class DataAccessObject {
	Connection con = null;
	static ResultSet rs;
	PreparedStatement pst = null;
	ConnectionPool pool = null;
	private int noOfRecords;

	public DataAccessObject() {
		try {
			pool = ConnectionPool.getInstance();
			System.out
					.println("Size of free pool in dao constructor b4 getting a conn::"
							+ ConnectionPool.freecon.size());
			System.out
					.println("Size of busy pool in dao constructor b4 getting a conn::"
							+ ConnectionPool.busycon.size());
			con = pool.getCon();
			System.out.println("Got a con");
			// pst = (PreparedStatement) con.createStatement();
			System.out.println("Checking whether the conn is closed");
			if (!con.isClosed())
				System.out.println("Successfully Connected!!!");
			System.out
					.println("Size of free pool in dao constructor after getting a conn::"
							+ ConnectionPool.freecon.size());
			System.out
					.println("Size of busy pool in dao constructor after getting a conn::"
							+ ConnectionPool.busycon.size());

		} catch (Exception e) {
		}
	}

	public boolean paymentGatewayCheck(User user) {
		boolean isValid = true;
		try {
			pst = con
					.prepareStatement("select vlp_id from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibpayment "
							+ "where vlp_cardnumberone = ? and vlp_cardnumbertwo = ? and vlp_cardnumberthree = ? "
							+ "and vlp_cardnumberfour = ? and vlp_cardcvv = ? and vlp_cardexpirydate = ? and vlp_nameoncard = ? and vlp_rowstate != -1");
			pst.setString(1, user.getPayment().getCardNumOne());
			pst.setString(2, user.getPayment().getCardNumTwo());
			pst.setString(3, user.getPayment().getCardNumThree());
			pst.setString(4, user.getPayment().getCardNumFour());
			pst.setString(5, user.getPayment().getCardCvv());
			pst.setString(6, user.getPayment().getCardExpDate());
			pst.setString(7, user.getPayment().getNameOnCard());
			ResultSet rs = pst.executeQuery();
			System.out.println(pst);
			while (!rs.next()) {
				isValid = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}

	public User authenticateSignIn(User user) throws SQLException {
		try {
			String query = "select vlusr_id,vlusr_displayname,vlusr_prefernce,"
					+ "vlusr_loginid,vlusr_password,vlusr_invalidlogins,vlusr_statusid from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibUsers where vlusr_loginid = ? and vlusr_rowstate != -1";
			pst = con.prepareStatement(query);
			pst.setString(1, user.getLoginId());
			rs = pst.executeQuery();

			while (rs.next()) {
				user.setId(rs.getInt(1));
				user.setDisplayName(rs.getString(2));
				user.getPreference().setPreferenceId(rs.getInt(3));
				user.setInvalidLogins(rs.getInt(6));
				user.getStatus().setStatusId(rs.getInt(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}
		return user;
	}

	public void updateInvalidLogin(String loginid) throws SQLException {
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		try {
			String query = "update  "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibUsers set vlusr_invalidlogins = vlusr_invalidlogins+1 , vlusr_lastdetailsupdate = ? "
					+ "where vlusr_loginid = ? and vlusr_rowstate != -1";
			pst = con.prepareStatement(query);
			pst.setDate(1, sqlToday);
			pst.setString(2, loginid);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			pool.freeCon(con);
		}
	}

	public String[] listState(String country) throws SQLException {
		String[] stateList = new String[1000];
		int countryid = 0;
		int i = 1;

		try {
			String queryStmt = "select vlc_countryid from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibCountryMaster where vlc_rowstate != -1 and vlc_countryname = ?";
			pst = con.prepareStatement(queryStmt);
			pst.setString(1, country);
			rs = pst.executeQuery();
			while (rs.next()) {
				countryid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String query = "select vls_statename from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibStateMaster where vls_rowstate != -1 and vls_countryid = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, countryid);
			rs = pst.executeQuery();
			while (rs.next()) {
				stateList[i] = rs.getString(1);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			pool.freeCon(con);
		}
		return stateList;
	}

	public String[] listCountry() throws SQLException {
		String[] countryList = new String[110];
		int i = 1;
		try {
			pst = con.prepareStatement("select vlc_countryname from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibCountryMaster where vlc_rowstate != -1");
			rs = pst.executeQuery();
			while (rs.next()) {
				countryList[i] = rs.getString(1);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			pool.freeCon(con);
		}
		System.out.println("List country size from dao::" + countryList.length);
		return countryList;
	}

	public String[] listCity(String state) throws SQLException {
		String[] cityList = new String[1000];
		int stateid = 0;
		int i = 1;
		try {
			pst = con
					.prepareStatement("select vls_stateid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".VideoLibStateMaster where vls_rowstate != -1 and vls_statename = ?");
			pst.setString(1, state);
			rs = pst.executeQuery();
			while (rs.next()) {
				stateid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pst = con
					.prepareStatement("select vlcm_cityname from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".VideoLibCityMaster where vlcm_rowstate != -1 and vlcm_stateid = ?");
			pst.setInt(1, stateid);
			rs = pst.executeQuery();
			while (rs.next()) {
				cityList[i] = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			pool.freeCon(con);
		}
		return cityList;
	}

	public int getpreferenceID(Preference pref) throws SQLException {
		int prefId = 0;
		try {
			pst = con
					.prepareStatement("select vlusrtype_id from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibusertypemaster where vlusrtype_name = ? and vlusrtype_statusid = 1 and"
							+ " vlusrtype_rowstate != -1");
			pst.setString(1, pref.getPreferenceType());
			rs = pst.executeQuery();
			while (rs.next()) {
				prefId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
		}
		return prefId;
	}

	public int getCountryID(Country country) throws SQLException {
		int countryId = 0;
		try {
			pst = con
					.prepareStatement("select vlc_countryid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibcountrymaster where vlc_countryname = ? and vlc_rowstate != -1");
			pst.setString(1, country.getCountryName());
			rs = pst.executeQuery();
			while (rs.next()) {
				countryId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
		}
		return countryId;
	}

	public int getStateID(State state, Country country) throws SQLException {
		int stateId = 0;
		try {
			pst = con
					.prepareStatement("select vls_stateid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibstatemaster where vls_statename = ? and vls_countryid = ? and vls_rowstate != -1");
			pst.setString(1, state.getStateName());
			pst.setInt(2, getCountryID(country));
			rs = pst.executeQuery();
			while (rs.next()) {
				stateId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
		}
		return stateId;
	}

	public int getCityID(State state, City city, Country country)
			throws SQLException {
		int cityId = 0;
		try {
			pst = con
					.prepareStatement("select vlcm_cityid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibcitymaster where vlcm_cityname = ? and vlcm_stateid = ? and vlcm_rowstate != -1");
			pst.setString(1, city.getCityName());
			pst.setInt(2, getStateID(state, country));
			rs = pst.executeQuery();
			while (rs.next()) {
				cityId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
		}
		return cityId;
	}

	public void SignUp(User user) throws SQLException {
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		try {
			pst = con
					.prepareStatement(constants.ConstantData.INSERT_VIDEOLIBUSERS);
			pst.setString(1, user.getSalutation());
			pst.setString(2, user.getFirstName());
			pst.setString(3, user.getMiddleName());
			pst.setString(4, user.getLastName());
			pst.setString(5, user.getDisplayName());
			pst.setString(6, user.getDob());
			pst.setString(7, user.getSex());
			pst.setInt(8, insertAddress(user.getAddress()));
			pst.setString(9, user.getLoginId());
			pst.setString(10, user.getPassword());
			pst.setDate(11, sqlToday);
			pst.setInt(12, constants.ConstantData.ACTIVE_STATUS_ID);
			pst.setInt(13, user.getPreference().getPreferenceId());
			pst.setString(14, user.getEmailId());
			pst.setInt(15, 0);
			pst.setDate(16, null);
			pst.setString(17, user.getSsnId());
			pst.setInt(18, user.getSubscriptionFee());
			pst.setDate(19, null);
			pst.setDate(20, sqlToday);
			pst.setDate(21, null);
			pst.setInt(22, 1);
			pst.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			pool.freeCon(con);
		}

	}

	/**
	 * Inserts the address of the user and returns the new generated addressID
	 * 
	 * @param address
	 * @return
	 * @throws SQLException
	 */
	public int insertAddress(Address address) throws SQLException {
		ResultSet rs = null;
		PreparedStatement pst = null;
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		int cityid = 0, countryid = 0, stateid = 0, maxAddrId = 1;
		try {

			pst = con
					.prepareStatement("select vlcm_cityid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".VideoLibCityMaster where vlcm_cityname = ? and vlcm_rowstate != -1");
			pst.setString(1, address.getCity().getCityName());
			rs = pst.executeQuery();
			while (rs.next()) {
				cityid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			rs = null;
			pst = con
					.prepareStatement("select vls_stateid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".VideoLibStateMaster where vls_statename = ? and vls_rowstate != -1");
			pst.setString(1, address.getState().getStateName());
			rs = pst.executeQuery();
			while (rs.next()) {
				stateid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			rs = null;
			pst = con
					.prepareStatement("select vlc_countryid from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".VideoLibCountryMaster where vlc_countryname = ? and vlc_rowstate != -1");
			pst.setString(1, address.getCountry().getCountryName());
			rs = pst.executeQuery();
			while (rs.next()) {
				countryid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			pst = con
					.prepareStatement(constants.ConstantData.INSERT_VIDEOLIBUSERADDRESSMASTER);
			pst.setString(1, address.getAddrLine1());
			pst.setString(2, address.getAddrLine2());
			pst.setString(3, address.getAddrLine3());
			pst.setString(4, address.getTelNo());
			pst.setInt(5, cityid);
			pst.setInt(6, stateid);
			pst.setInt(7, countryid);
			pst.setString(8, address.getZipCode());
			pst.setDate(9, sqlToday);
			pst.setInt(10, 1);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			rs = null;
			pst = con.prepareStatement("select MAX(vlusraddress_id) from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibUserAddressMaster");
			rs = pst.executeQuery();
			while (rs.next()) {
				maxAddrId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
		}
		return maxAddrId;
	}

	public User[] displayUsers(int preferenceId, int offset, int noOfRecords)
			throws SQLException {
		ArrayList<User> users = new ArrayList<User>();

		try {
			if (!con.isClosed()) {
				/*
				 * this case is for when user enters nothing and searches for
				 * which we have to display all the users
				 */
				ResultSet rt = null;
				if (preferenceId == 0) {
					System.out.println("Fetching all users");
					pst = con
							.prepareStatement("select SQL_CALC_FOUND_ROWS * from videolibrary.VideoLibUsers where vlusr_rowstate !=-1 and vlusr_statusid = 1");

				} /* this one is for all users */

				else {
					System.out.println("Fetchin all members with pref id::"
							+ preferenceId);
					pst = con
							.prepareStatement("select * from videolibrary.VideoLibUsers where vlusr_rowstate !=-1 and vlusr_statusid = 1 and vlusr_prefernce=?");
					pst.setInt(1, preferenceId);
				}
				rt = pst.executeQuery();

				while (rt.next()) {
					User user = new User();
					user.setId(rt.getInt(1));
					user.setSalutation(rt.getString(2));
					user.setFirstName(rt.getString(3));
					user.setMiddleName(rt.getString(4));
					user.setLastName(rt.getString(5));
					user.setDisplayName(rt.getString(6));
					user.setDob(String.valueOf(rt.getString(7)));
					user.setSex(rt.getString(8));
					user.setLoginId(rt.getString(10));
					user.setLastPwdUpdate(String.valueOf(rt.getDate(12)));
					user.setEmailId(rt.getString(15));
					user.setLastLogin(String.valueOf(rt.getDate(17)));
					user.setSubscriptionRenewal(String.valueOf(rt.getDate(18)));
					users.add(user);

				} /* end of while */
				rt.close();
				rt = pst.executeQuery("SELECT FOUND_ROWS()");
				if (rs.next())
					this.setNoOfRecords(rs.getInt(1));

			} /* this one is for end of if */
			/* this one is for if user enters particular userid */

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			pst.close();
			pool.freeCon(con);
		}
		System.out.println("inside method size=" + users.size());
		User[] userArray = new User[users.size()];
		userArray = (User[]) users.toArray(userArray);
		return userArray;

	} /* end of method for user */

	/**
	 * @return the noOfRecords
	 */
	public int getNoOfRecords() {
		return noOfRecords;
	}

	/**
	 * @param noOfRecords
	 *            the noOfRecords to set
	 */
	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public ArrayList<Movie> getLatestMovies(ArrayList<Movie> latestMovieDetails)
			throws SQLException {
		try {
			if (!con.isClosed()) {
				pst = con
						.prepareStatement("select vlmov_id , vlmov_name , vlmt_availablecopies from "
								+ constants.ConstantData.SCHEMA_NAME
								+ ".videolibmoviesmaster "
								+ "where vlmov_releasedate between current_date and current_date + interval '1' month");
				rs = pst.executeQuery();
				while (rs.next()) {
					Movie movie = new Movie();
					movie.setMovieId(rs.getInt(1));
					movie.setMovieName(rs.getString(2));
					movie.setAvailableCopies(rs.getInt(3));
					latestMovieDetails.add(movie);
				}
			} /* this one is closing if brace */
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
			System.out.println("Size of free pool::"
					+ ConnectionPool.freecon.size());
			System.out.println("Size of busy pool::"
					+ ConnectionPool.busycon.size());

		}
		return latestMovieDetails;
	}

	public ArrayList<Movie> displayMovies(String filterAlphabet)
			throws SQLException {
		ArrayList<Movie> displayMovieDetails = new ArrayList<Movie>();
		try {
			if (!con.isClosed()) {
				pst = con
						.prepareStatement("select mv.vlmov_id , mv.vlmov_name , mv.vlmov_production , mv.vlmov_releasedate , mct.vlmcat_id , "
								+ "mct.vlmcat_name , mv.vlmt_availablecopies, mv.vlmov_rentamount from "
								+ constants.ConstantData.SCHEMA_NAME
								+ ".videolibmoviesmaster mv left outer join "
								+ constants.ConstantData.SCHEMA_NAME
								+ ".videolibmoviecategory mct on mv.vlmov_categoryid = mct.vlmcat_id "
								+ "where mv.vlmov_name like ? and mv.vlmov_rowstate != -1 and mct.vlmcat_rowstate != -1");
				String filterAlphabet1 = filterAlphabet + "%";
				pst.setString(1, filterAlphabet1);

				System.out.println(pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					Movie movie = new Movie();
					MovieCategory movieCategory = new MovieCategory();
					movie.setMovieId(rs.getInt("mv.vlmov_id"));
					movie.setMovieName(rs.getString("mv.vlmov_name"));
					movie.setProdBanner(rs.getString("mv.vlmov_production"));
					movie.setReleaseDate(rs.getString("mv.vlmov_releasedate"));
					movieCategory
							.setMovieCategoryId(rs.getInt("mct.vlmcat_id"));
					movieCategory.setMovieCategory(rs
							.getString("mct.vlmcat_name"));
					movie.setMovieCategory(movieCategory);
					movie.setAvailableCopies(rs
							.getInt("mv.vlmt_availablecopies"));
					movie.setRentAmount(rs.getInt("mv.vlmov_rentamount"));
					displayMovieDetails.add(movie);
				}// System.out.println("size in::::::::::::::::::::" +
					// displayMovieDetails.size());
			} /* this one is closing if brace */
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
			System.out.println("Size of free pool::"
					+ ConnectionPool.freecon.size());
			System.out.println("Size of busy pool::"
					+ ConnectionPool.busycon.size());

		}
		return displayMovieDetails;
	}

	public User userExistance(User user) throws SQLException {
		String errorMsg = "Registration issue kindly connect to administrator";
		ResultSet rs = null;
		int userid = 0;
		try {
			pst = con
					.prepareStatement("select vlusr_id from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibusers where vlusr_id = ? and vlusr_rowstate != -1");
			pst.setInt(1, user.getId());
			rs = pst.executeQuery();
			while (rs.next())
				userid = rs.getInt("vlusr_id");
			if (userid == 0)
				user.setErrorMsg(errorMsg);
			else
				user.setErrorMsg("Success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			rs.close();
			pool.freeCon(con);
		}
		return user;
	}

	/**
	 * 
	 * @param movieCart
	 * @return
	 * @throws SQLException
	 */
	public MovieCart[] movieExistance(MovieCart[] movieCart)
			throws SQLException {
		String errorMsg = "Movie no longer available in the library";
		try {
			for (int i = 0; i < movieCart.length; i++) {
				System.out.println(i);
				ResultSet rs = null;
				PreparedStatement pst = con
						.prepareStatement("select vlmov_id , vlmt_availablecopies from "
								+ constants.ConstantData.SCHEMA_NAME
								+ ".videolibmoviesmaster where vlmov_id = ? and vlmov_rowstate != -1");
				pst.setInt(1, movieCart[i].getMovieId());
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getInt(2) <= 0)
						movieCart[i].setErrorMsg(errorMsg);
				}
				// while (!rs.next()) {
				// movieCart[i].setErrorMsg(errorMsg);
				// }
				pst.close();
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}
		return movieCart;
	}

	/**
	 * 
	 * @param movieCart
	 * @return
	 * @throws SQLException
	 */
	public MovieCart[] userMovieMapExistance(MovieCart[] movieCart)
			throws SQLException {
		PreparedStatement[] pst = new PreparedStatement[movieCart.length];
		ResultSet rs[] = new ResultSet[movieCart.length];
		try {
			for (int i = 0; i < movieCart.length; i++) {
				pst[i] = con
						.prepareStatement("select vlmt_cartstatus from "
								+ constants.ConstantData.SCHEMA_NAME
								+ ".VideoLibMovieCart where vlmt_movieid = ? and vlmt_userid = ? and vlmt_rowstate != -1 ");
				// pst[i] = con
				// .prepareStatement("select vlmt_movieid , vlmt_userid , vlmt_addedon , vlmt_cartstatus , vlmt_itemstatus from "
				// + constants.ConstantData.SCHEMA_NAME
				// +
				// ".VideoLibMovieCart where vlmt_movieid = ? and vlmt_userid = ? ((vlmt_cartstatus = 1 and vlmt_itemstatus = 'B') or (vlmt_cartstatus = 1 and vlmt_itemstatus)) and vlmt_rowstate != -1 ");
				pst[i].setInt(1, movieCart[i].getMovieId());
				pst[i].setInt(2, movieCart[i].getUserId());
				rs[i] = pst[i].executeQuery();
				while (rs[i].next()) {
					// if ((rs[i].getString(5).equals("B"))
					// && (rs[i].getInt(4) == 1))
					// movieCart[i]
					// .setErrorMsg("Movie is already available in your account.Kindly return it to store on time");
					// else if ((rs[i].getString(5).equals("C"))
					// && (rs[i].getInt(4) == 1))
					// movieCart[i]
					// .setErrorMsg("Movie is already added in your cart");
					// }

					if (rs[i].getString(1).equals("B"))
						movieCart[i]
								.setErrorMsg("Movie is already available in your account!");
					else if (rs[i].getString(5).equals("C"))
						movieCart[i]
								.setErrorMsg("Movie is already added in your cart");
				}
				pst[i].close();
				rs[i].close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}

		return movieCart;
	}

	/**
	 * 
	 * @param movieCart
	 * @return
	 * @throws SQLException
	 */
	public MovieCart[] addToCart(MovieCart[] movieCart) throws SQLException {
		System.out.println("Into Add to Cart");
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		System.out.println("test");
		PreparedStatement pst[] = new PreparedStatement[movieCart.length];
		// int i = movieCart.length;
		try {
			System.out.println("test2");
			User user = new User();
			user.setId(movieCart[0].getUserId());
			user = userExistance(user);
			if (user.getErrorMsg() != "Success") {
				movieCart[0]
						.setErrorMsg("Registration issue kindly connect to administrator");
				return movieCart;
			}
			System.out.println("User authenticated!");
			movieCart = movieExistance(movieCart);
			System.out.println("Movie authenticated!");
			// for(i = 0 ; i <= movieCart.length ; i++){
			// if(movieCart[i].getErrorMsg() != null) return movieCart;
			// }
			movieCart = userMovieMapExistance(movieCart);
			System.out
					.println("Movie not already present! Going to insert in cart!");
			// for(i = 0 ; i <= movieCart.length ; i++){
			// if(movieCart[i].getErrorMsg() != null) return movieCart;
			// }
			for (int i = 0; i < movieCart.length
					&& movieCart[i].getErrorMsg() == null; i++) {
				// i = 0;
				// while ((i != movieCart.length)
				// && (movieCart[i].getErrorMsg() == null)) {
				pst[i] = con
						.prepareStatement(constants.ConstantData.INSERT_VIDEOLIBMOVIECART);
				pst[i].setInt(1, movieCart[i].getMovieId());
				pst[i].setInt(2, movieCart[i].getUserId());
				pst[i].setDate(3, sqlToday);
				pst[i].setString(4, "C");
				// pst[i].setInt(5, 1);
				pst[i].setDate(5, sqlToday);
				pst[i].setInt(6, 1);
				pst[i].setInt(7, 1); // Qty
				pst[i].executeUpdate();
				// i++;
				pst[i].close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		System.out.println("Returning the updated cart array");
		return movieCart;
	}

	/**
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public MovieCart[] retrieveCart(int userId) throws SQLException {
		ArrayList<MovieCart> movieCartArrayList = new ArrayList<MovieCart>();
		// ArrayList<MovieCart> movieCartArrayList = new ArrayList<MovieCart>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String query = "select vlmt_movieid, vlmt_qty from "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".VideoLibMovieCart "
					+ "where vlmt_userid = ? and vlmt_rowstate != -1 and vlmt_cartstatus = 'C'";
			pst = con.prepareStatement(query);
			pst.setInt(1, userId);
			rs = pst.executeQuery();
			while (rs.next()) {
				MovieCart movieCart = new MovieCart();
				movieCart.setMovieId(rs.getInt(1));
				movieCart.setQty(rs.getInt(2));
				movieCartArrayList.add(movieCart);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}
		MovieCart[] movieCartArray = new MovieCart[movieCartArrayList.size()];
		return movieCartArrayList.toArray(movieCartArray);
	}

	/**
	 * 
	 * @param cartData
	 * @return
	 * @throws SQLException
	 */
	public Movie[] retrieveMovieDataForCart(MovieCart[] cartData)
			throws SQLException {
		Movie[] movieArray = new Movie[cartData.length];
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			for (int i = 0; i < movieArray.length; i++) {
				int movieId = cartData[i].getMovieId();
				String query = "select vlmov_name, vlmov_production, vlmov_rentamount, vlmov_id from "
						+ constants.ConstantData.SCHEMA_NAME
						+ ".videolibmoviesmaster "
						+ "where vlmov_id = ? and vlmov_rowstate != -1";
				pst = con.prepareStatement(query);
				pst.setInt(1, movieId);
				rs = pst.executeQuery();
				while (rs.next()) {
					Movie movie = new Movie();
					movie.setMovieName(rs.getString(1));
					movie.setProdBanner(rs.getString(2));
					movie.setRentAmount(rs.getInt(3));
					movie.setMovieId(rs.getInt(4));
					movieArray[i] = movie;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}
		return movieArray;

	}

	public void deleteFromCart(int userID, int movieID) throws SQLException {
		try {
			String query = "update "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".videolibmoviecart set vlmt_rowstate = -1 where vlmt_userid = ? and vlmt_movieid = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, userID);
			pst.setInt(2, movieID);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);

		}
	}

	public void addBalance(int userID, float amountToAdd) throws SQLException {
		try {
			String query = "update "
					+ constants.ConstantData.SCHEMA_NAME
					+ ".videolibusers set vlusr_subscriptionfee = vlusr_subscriptionfee + ? "
					+ "where vlusr_id = ? and vlusr_rowstate != -1";
			pst = con.prepareStatement(query);
			pst.setInt(2, userID);
			pst.setFloat(1, amountToAdd);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeCon(con);
		}
	}

	// ***************************************************************************************************
	public Movie[] checkOutMovie(Movie[] movie, User user) throws SQLException {
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		PreparedStatement pst = null;
		PreparedStatement[] pst1 = new PreparedStatement[movie.length];
		int i = 0;
		String appendPst = "";
		ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null;
		ResultSet[] resultSetArray = new ResultSet[movie.length];
		int userMovieAccountCount = 0;
		try {

			// Checking the preference ID of user
			pst = con
					.prepareStatement("select vlusr_prefernce from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibusers where vlusr_id = ? and vlusr_rowstate != -1 and vlusr_statusid = 1");
			pst.setInt(1, user.getId());
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				if (rs1.getInt(1) == 1) {
					System.out.println("Simple User");
					if (movie.length <= 2) {
						System.out
								.println(" Fetching the already bought count for user");
						pst = con
								.prepareStatement("select count(vlmt_movieid) from "
										+ constants.ConstantData.SCHEMA_NAME
										+ ".videolibmoviecart where vlmt_userid = ? and vlmt_rowstate != -1 and vlmt_cartstatus = ?");
						pst.setInt(1, user.getId());
						pst.setString(2, "B");
						rs2 = pst.executeQuery();
						while (rs2.next()) {
							userMovieAccountCount = rs2.getInt(1);
						}
						System.out.println("Already bought movies count:"
								+ userMovieAccountCount);
						if (userMovieAccountCount + movie.length <= 2) {
							System.out
									.println("userMovieAccountCount + movie.length <= 2... Hence can proceed");
							System.out.println("Checking the balance");
							pst = con
									.prepareStatement("select vlusr_subscriptionfee from "
											+ constants.ConstantData.SCHEMA_NAME
											+ ".videolibusers where vlusr_id = ? and vlusr_rowstate != -1 and vlusr_statusid = 1");
							pst.setInt(1, user.getId());
							rs3 = pst.executeQuery();
							while (rs3.next()) {
								if (rs3.getDouble(1) > 0) {
									System.out.println("balance "
											+ rs3.getDouble(1)
											+ " greater than 0.. valid");
									System.out
											.println("Fetching the total rent amount of all the movies for user");
									for (i = 0; i < movie.length - 1; i++)
										appendPst = appendPst.concat(",?");
									pst = con
											.prepareStatement("select SUM(vlmov_rentamount) from "
													+ constants.ConstantData.SCHEMA_NAME
													+ ".videolibmoviesmaster where vlmov_id in (?"
													+ appendPst + ")");
									for (i = 0; i < movie.length; i++)
										pst.setInt(i + 1, movie[i].getMovieId());
									rs4 = pst.executeQuery();
									while (rs4.next()) {
										System.out
												.println("total rent amount for the movies to be added: "
														+ rs4.getInt(1));
										if (rs4.getInt(1) <= rs3.getDouble(1)) {
											System.out
													.println("Total rent amount less than balance.. valid");
											System.out
													.println("Checking the avl copies of all movies");
											for (i = 0; i < movie.length; i++) {
												pst1[i] = con
														.prepareStatement("select vlmt_availablecopies from "
																+ constants.ConstantData.SCHEMA_NAME
																+ ".videolibmoviesmaster where vlmov_id = ? and vlmov_rowstate != -1");
												pst1[i].setInt(1,
														movie[i].getMovieId());
												resultSetArray[i] = pst1[i]
														.executeQuery();
												while (resultSetArray[i].next()) {
													System.out
															.println("Avl copies for "
																	+ movie[i]
																			.getMovieId()
																	+ " are "
																	+ resultSetArray[i]
																			.getInt(1));
													if (resultSetArray[i]
															.getInt(1) > 0) {
														System.out
																.println("Inserting into simple user trxn table");
														pst1[i] = con
																.prepareStatement("insert into "
																		+ constants.ConstantData.SCHEMA_NAME
																		+ ".videolibsimpleusertransaction (vlsut_usrid , vlsut_rentedmovie , vlsut_moviereturned , vlsut_payableamount , vlsut_amountpaid , vlsut_createddate , vlsut_rowstate) values (?,?,?,?,?,?,?)");
														pst1[i].setInt(1,
																user.getId());
														pst1[i].setInt(
																2,
																movie[i].getMovieId());
														pst1[i].setInt(3, 0);
														pst1[i].setInt(4, 0);
														pst1[i].setDouble(
																5,
																getPayableAmount(movie[i]
																		.getMovieId()));
														pst1[i].setDate(6,
																sqlToday);
														pst1[i].setInt(7, 1);
														pst1[i].executeUpdate();
														System.out
																.println("Updating B in corresponding CArt entry");
														pst1[i] = con
																.prepareStatement("update "
																		+ constants.ConstantData.SCHEMA_NAME
																		+ ".videolibmoviecart set vlmt_cartstatus = 'B' where vlmt_userid = ? and vlmt_movieid = ? and vlmt_rowstate != -1");
														pst1[i].setInt(1,
																user.getId());
														pst1[i].setInt(
																2,
																movie[i].getMovieId());
														pst1[i].executeUpdate();
														System.out
																.println("Reducing the avl copy for movie "
																		+ movie[i]
																				.getMovieId());
														pst1[i] = con
																.prepareStatement("update "
																		+ constants.ConstantData.SCHEMA_NAME
																		+ ".videolibmoviesmaster set vlmt_availablecopies = vlmt_availablecopies-1 where vlmov_id = ? and vlmov_rowstate != -1");
														pst1[i].executeUpdate();
														pst1[i].close();
													} else {
														System.out
																.println("Invalid - Movie not available in the store");
														movie[i].setErrorMsg("Invalid - Movie not available in the store");
													}

												}
											}
										} else {
											System.out
													.println("Invalid - Insufficient balance kindly recharge your account");
											movie[0].setErrorMsg("Invalid - Insufficient balance kindly recharge your account");
											return movie;
										}
									}
								} else {
									System.out
											.println("Invalid - Insufficient balance kindly recharge your account");
									movie[0].setErrorMsg("Invalid - Insufficient balance kindly recharge your account");
									return movie;
								}
							}
						} else {
							while (i <= movie.length) {
								System.out
										.println("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
								movie[0].setErrorMsg("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
								i++;
							}
							return movie;
						}
					} else {
						while (i <= movie.length) {
							System.out
									.println("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
							movie[0].setErrorMsg("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
							i++;
						}
						return movie;
					}
				}
				if (rs1.getInt(1) == 2) {
					System.out.println("Premium user");
					if (movie.length <= 10) {
						System.out
								.println("Movie to be added is not greater than 10... valid");
						System.out
								.println("getting all the bought movies count for user");
						pst = con
								.prepareStatement("select count(vlmt_movieid) from "
										+ constants.ConstantData.SCHEMA_NAME
										+ ".videolibmoviecart where vlmt_userid = ? and vlmt_rowstate != -1 and vlmt_cartstatus = ?");
						pst.setInt(1, user.getId());
						pst.setString(2, "B");
						rs2 = pst.executeQuery();
						while (rs2.next()) {
							System.out
									.println("Count of already bought movies:"
											+ rs2.getInt(1));
							userMovieAccountCount = rs2.getInt(1);
						}
						if (userMovieAccountCount + movie.length <= 10) {
							System.out
									.println("sum of already bought n to be added is !> 10.. valid");
							// System.out.println("checking balance");
							// pst = con
							// .prepareStatement("select vlusr_subscriptionfee from "
							// + constants.ConstantData.SCHEMA_NAME
							// +
							// ".videolibusers where vlusr_id = ? and vlusr_rowstate != -1 and vlusr_status = 1");
							// pst.setInt(1, user.getId());
							// rs3 = pst.executeQuery();
							// while (rs3.next()) {
							// if (rs3.getDouble(1) > 0) {s
							// for (i = 0; i < movie.length - 1; i++)
							// appendPst = appendPst.concat(",?");
							// pst = con
							// .prepareStatement("select SUM(vlmov_rentamount) from "
							// + constants.ConstantData.SCHEMA_NAME
							// + ".videolibmoviesmaster where vlmov_id in (?"
							// + appendPst + ")");
							// for (i = 0; i < movie.length; i++)
							// pst.setInt(i + 1, movie[i].getMovieId());
							// rs4 = pst.executeQuery();
							// while (rs4.next()) {
							// if (rs4.getInt(1) <= rs3.getDouble(1)) {
							System.out
									.println("Checking for the avalilable copies for all movies...");
							for (i = 0; i < movie.length; i++) {
								pst1[i] = con
										.prepareStatement("select vlmt_availablecopies from "
												+ constants.ConstantData.SCHEMA_NAME
												+ ".videolibmoviesmaster where vlmov_id = ? and vlmov_rowstate != -1");
								pst1[i].setInt(1, movie[i].getMovieId());
								resultSetArray[i] = pst1[i].executeQuery();
								pst1[i].close();
								while (resultSetArray[i].next()) {
									System.out.println("Avl copies for movies "
											+ movie[i].getMovieId() + " is "
											+ resultSetArray[i].getInt(1));
									if (resultSetArray[i].getInt(1) > 0) {
										System.out
												.println("Inserting into premium transaction table");
										pst1[i] = con
												.prepareStatement("insert into "
														+ constants.ConstantData.SCHEMA_NAME
														+ ".videolibpremiumusertransaction (vlput_usrid , vlput_rentedmovie , vlput_moviereturned , vlput_availablebalance , vlput_totaldeduction , vlput_createddate , vlput_rowstate) values (?,?,?,?,?,?,?)");
										pst1[i].setInt(1, user.getId());
										pst1[i].setInt(2, movie[i].getMovieId());
										pst1[i].setInt(3, 0);
										pst1[i].setInt(4, 0);
										pst1[i].setDouble(5,
												getPayableAmount(movie[i]
														.getMovieId()));
										pst1[i].setDate(6, sqlToday);
										pst1[i].setInt(7, 1);
										pst1[i].executeUpdate();
										System.out
												.println("Updating B in corresponding CArt entry");
										pst1[i] = con
												.prepareStatement("update "
														+ constants.ConstantData.SCHEMA_NAME
														+ ".videolibmoviecart set vlmt_cartstatus = 'B' where vlmt_userid = ? and vlmt_movieid = ? and vlmt_rowstate != -1");
										pst1[i].setInt(1, user.getId());
										pst1[i].setInt(2, movie[i].getMovieId());
										pst1[i].executeUpdate();
										System.out
												.println("Reducing the avl copy for movie "
														+ movie[i].getMovieId());
										pst1[i] = con
												.prepareStatement("update "
														+ constants.ConstantData.SCHEMA_NAME
														+ ".videolibmoviesmaster set vlmt_availablecopies = vlmt_availablecopies-1 where vlmov_id = ? and vlmov_rowstate != -1");
										pst1[i].executeUpdate();
										pst1[i].close();
									} else {
										System.out
												.println("Invalid - Movie not available in the store");
										movie[i].setErrorMsg("Invalid - Movie not available in the store");
									}
								}
							}
							// } else {
							// System.out.println("Invalid - Insufficient balance kindly recharge your account");
							// movie[0].setErrorMsg("Invalid - Insufficient balance kindly recharge your account");
							// return movie;
							// }
							// }
							// } else {
							// System.out.println("Invalid - Insufficient balance kindly recharge your account");
							// movie[0].setErrorMsg("Invalid - Insufficient balance kindly recharge your account");
							// return movie;
							// }

						} else {
							while (i <= movie.length) {
								movie[0].setErrorMsg("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
								i++;
							}
							System.out
									.println("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
							return movie;
						}
					} else {
						while (i <= movie.length) {
							movie[0].setErrorMsg("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
							i++;
						}
						System.out
								.println("Invalid - Your account exceeded the maximum number of movies.kindly return the previous movie item");
						return movie;
					}
				}
				// if()
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			// rs1.close();
			// rs2.close();
			pool.freeCon(con);
		}
		return movie;
	}

	public double getPayableAmount(int movieid) throws SQLException {
		ResultSet rs = null;
		double movieAmount = 0;
		try {
			pst = con
					.prepareStatement("select vlmov_rentamount from "
							+ constants.ConstantData.SCHEMA_NAME
							+ ".videolibmoviesmaster where vlmov_id = ? and vlmov_rowstate != -1");
			pst.setInt(1, movieid);
			rs = pst.executeQuery();
			while (rs.next()) {
				movieAmount = rs.getDouble(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pst.close();
			pool.freeCon(con);
		}
		return movieAmount;
	}

}
