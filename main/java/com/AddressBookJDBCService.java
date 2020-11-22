package com;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookJDBCService {
	List<AddressBookData> addressBookDataObj = null;
	private static AddressBookJDBCService addressBookJDBCService;

	public static AddressBookJDBCService getInstance() {
		if (addressBookJDBCService == null) {
			addressBookJDBCService = new AddressBookJDBCService();
		}
		return addressBookJDBCService;
	}

	public Connection getConnection() throws AddressBookException {
		String jdbcURL = "jdbc:mysql://localhost:3306/addressbook_system?useSSL=false";
		String user = "root";
		String password = "admin123";
		Connection connection;
		System.out.println("Connecting to database: " + jdbcURL);
		try {
			connection = DriverManager.getConnection(jdbcURL, user, password);
			System.out.println("Connection is SuccessFull! " + connection);
			return connection;
		} catch (SQLException e) {
			throw new AddressBookException("Unable to establish the connection");
		}
	}

	public List<AddressBookData> readData() throws AddressBookException {
		String sql = "SELECT * FROM address_book;";
		List<AddressBookData> addressBookList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String first_name = resultSet.getString("first_name");
				String last_name = resultSet.getString("last_name");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				int zip = resultSet.getInt("zip");
				long phone_number = resultSet.getLong("phone_number");
				String email_id = resultSet.getString("email_id");
				String addressbook_name = resultSet.getString("addressbook_name");
				String addressbook_type = resultSet.getString("addressbook_type");
				java.sql.Date date = resultSet.getDate("date_added");
				addressBookList.add(new AddressBookData(first_name, last_name, address, city, state, zip, phone_number,
						email_id, addressbook_name, addressbook_type, date));
			}
		} catch (SQLException e) {
			throw new AddressBookException("Unable to retrieve data");
		}
		return addressBookList;
	}

	public int updateAddressBookDataUsingPreparedStatement(String state, int zip, String name)
			throws AddressBookException {
		String query = "UPDATE address_book SET state = ? , zip = ? where first_name = ?";
		try (Connection con = this.getConnection()) {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, state);
			preparedStatement.setInt(2, zip);
			preparedStatement.setString(3, name);
			int result = preparedStatement.executeUpdate();
			addressBookDataObj = getAddressBookDataFromDB(name);
			if (result > 0 && addressBookDataObj != null) {
				((AddressBookData) addressBookDataObj).setState(state);
				((AddressBookData) addressBookDataObj).setZip(zip);
			}
		} catch (SQLException e) {
			throw new AddressBookException("Error in updation");
		}
		return addressBookDataObj.size();
	}

	public List<AddressBookData> getAddressBookDataFromDB(String name) throws AddressBookException {
		String sql = "SELECT * FROM address_book WHERE first_name=?;";
		List<AddressBookData> addressBookList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String first_name = resultSet.getString("first_name");
				String last_name = resultSet.getString("last_name");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				int zip = resultSet.getInt("zip");
				long phone_number = resultSet.getLong("phone_number");
				String email_id = resultSet.getString("email_id");
				String addressbook_name = resultSet.getString("addressbook_name");
				String addressbook_type = resultSet.getString("addressbook_type");
				java.sql.Date date = resultSet.getDate("date_added");
				addressBookList.add(new AddressBookData(first_name, last_name, address, city, state, zip, phone_number,
						email_id, addressbook_name, addressbook_type, date));
			}
		} catch (SQLException e) {
			throw new AddressBookException("Unable to get data.Please check table for updation");
		}
		return addressBookList;
	}

	private List<AddressBookData> getAddressBookListFromResultset(ResultSet resultSet) throws AddressBookException {
		List<AddressBookData> addressBookList = new ArrayList<AddressBookData>();
		try {
			while (resultSet.next()) {
				String first_name = resultSet.getString("first_name");
				String last_name = resultSet.getString("last_name");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				int zip = resultSet.getInt("zip");
				long phone_number = resultSet.getLong("phone_number");
				String email_id = resultSet.getString("email_id");
				String addressbook_name = resultSet.getString("addressbook_name");
				String addressbook_type = resultSet.getString("addressbook_type");
				java.sql.Date date = resultSet.getDate("date_added");
				addressBookList.add(new AddressBookData(first_name, last_name, address, city, state, zip, phone_number,
						email_id, addressbook_name, addressbook_type, date));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressBookList;
	}

	public List<AddressBookData> getAdressBookDataByStartingDate(LocalDate startDate, LocalDate endDate)
			throws AddressBookException {
		String sql = String.format(
				"SELECT * FROM address_book WHERE date_added BETWEEN cast('%s' as date) and cast('%s' as date);",
				startDate.toString(), endDate.toString());
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return this.getAddressBookListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException("Connection Failed.");
		}
	}

	public Map<String, Integer> getContactsByCityOrState() throws AddressBookException {
		Map<String, Integer> contactByCityOrStateMap = new HashMap<>();
		ResultSet resultSet;
		String sqlCity = "SELECT city, count(first_name) as count FROM address_book GROUP BY city; ";
		String sqlState = "SELECT state, count(first_name) as count FROM address_book GROUP BY state; ";
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlCity);
			while (resultSet.next()) {
				String city = resultSet.getString("city");
				Integer count = resultSet.getInt("count");
				contactByCityOrStateMap.put(city, count);
			}
			resultSet = statement.executeQuery(sqlState);
			while (resultSet.next()) {
				String state = resultSet.getString("state");
				Integer count = resultSet.getInt("count");
				contactByCityOrStateMap.put(state, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactByCityOrStateMap;
	}

	public void addContact(String firstName, String lastName, String address, String city, String state, int zip,
			long phone, String email, String addressBookName, String addressBookType, Date dateAdded)
			throws AddressBookException {
		Connection connection = null;
		connection = this.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new AddressBookException("Unable to AutoCommit");
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO address_book " + "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')", firstName,
					lastName, address, city, state, zip, phone, email, dateAdded);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new AddressBookException("Unable to Roll back");
			}
			throw new AddressBookException("Unable to get data.Please check table for updation");
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO address_book " + "VALUES ('%s', '%s')", email, addressBookName);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new AddressBookException("Unable to Roll Back in addressbook_name");
			}
			throw new AddressBookException("Unable to get data.Please check table for updation in address_name");
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new AddressBookException("Unable to commit");
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new AddressBookException("Unable to get data.Please check table for updation");
				}
		}
		addressBookDataObj.add(new AddressBookData(firstName, lastName, address, city, state, zip, phone, email,
				addressBookName, addressBookType, dateAdded));
	}

	public void addContactToDBWithThreads(List<AddressBookData> asList) {
		Map<Integer, Boolean> addressAdditionStatus = new HashMap<Integer, Boolean>();
		addressBookDataObj.forEach(addressbookdata -> {
			Runnable task = () -> {
				addressAdditionStatus.put(addressbookdata.hashCode(), false);
				System.out.println("Contact Being Added:" + Thread.currentThread().getName());
				try {
					this.addContact(addressbookdata.getFirst_name(), addressbookdata.getLast_name(),
							addressbookdata.getAddress(), addressbookdata.getCity(), addressbookdata.getState(),
							addressbookdata.getZip(), addressbookdata.getPhone_number(), addressbookdata.getEmail_id(),
							addressbookdata.getAddressbook_name(), addressbookdata.getAddressbook_type(),
							addressbookdata.getDate());
				} catch (AddressBookException e) {
					e.printStackTrace();
				}
				addressAdditionStatus.put(addressbookdata.hashCode(), true);
				System.out.println("Contact Added:" + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, addressbookdata.getFirst_name());
			thread.start();
		});
		while (addressAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		System.out.println(this.addressBookDataObj);
	}

	public int countEntries() {
		return addressBookDataObj.size();
	}
}