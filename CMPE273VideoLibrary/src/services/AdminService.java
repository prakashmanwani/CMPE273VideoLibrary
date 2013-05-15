package services;

import java.sql.SQLException;

import databaseConnection.DataAccessObject;

import model.User;

public class AdminService {
	DataAccessObject dao = new DataAccessObject();

	public User[] displayUsers(int preferenceId, int offset, int noOfRecords) throws SQLException {
		return dao.displayUsers(preferenceId,offset,noOfRecords);
	}
		
}
