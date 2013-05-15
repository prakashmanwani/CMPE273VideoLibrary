/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package services;

public interface Service extends java.rmi.Remote {
    public model.User signIn(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String[] listState(java.lang.String country) throws java.rmi.RemoteException;
    public java.lang.String[] listCountry() throws java.rmi.RemoteException;
    public java.lang.String[] listCity(java.lang.String state) throws java.rmi.RemoteException;
    public java.lang.String signUp(model.User user) throws java.rmi.RemoteException;
    public model.User[] displayUsers(int preferenceId, int offset, int noOfRecords) throws java.rmi.RemoteException;
    public model.Movie[] getLatestMovies() throws java.rmi.RemoteException;
    public model.Movie[] displayMovies(java.lang.String filterAlphabet) throws java.rmi.RemoteException;
    public model.MovieCart[] addToCart(model.MovieCart[] movieCart) throws java.rmi.RemoteException;
    public model.MovieCart[] retrieveCart(int userId) throws java.rmi.RemoteException;
    public model.Movie[] retrieveMovieDataForCart(model.MovieCart[] cartData) throws java.rmi.RemoteException;
    public void deleteFromCart(int userID, int movieID) throws java.rmi.RemoteException;
    public void closeAllConn() throws java.rmi.RemoteException;
    public boolean paymentGatewayCheck(model.User user) throws java.rmi.RemoteException;
    public void addBalance(int userID, float amountToAdd) throws java.rmi.RemoteException;
    public model.Movie[] checkOutMovie(model.Movie[] movie, model.User user) throws java.rmi.RemoteException;
}
