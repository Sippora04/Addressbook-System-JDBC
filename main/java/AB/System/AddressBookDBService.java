package AB.System;

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

public class AddressBookDBService {
	private PreparedStatement ContactDataStatement;
	private static AddressBookDBService addressBookDBService;

	private AddressBookDBService() {
	}

	static AddressBookDBService getInstance() {
		if (addressBookDBService == null) {
			addressBookDBService = new AddressBookDBService();
		}
		return addressBookDBService;
	}

	public Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/addressbook_system?useSSL=false";
		String userName = "root";
		String password = "admin123";
		Connection connection;
		System.out.println("connecting to database: " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("connection successful !!!! " + connection);
		return connection;
	}

	public List<PersonInfo> readData() {
		String sql = "SELECT * from address_book;";
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	private List<PersonInfo> getContactDetailsUsingSqlQuery(String sql) {
		List<PersonInfo> ContactList = null;
		try (Connection connection = addressBookDBService.getConnection();) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result = preparedStatement.executeQuery(sql);
			ContactList = this.getAddressBookData(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ContactList;
	}

	private List<PersonInfo> getAddressBookData(ResultSet result) {
		List<PersonInfo> contactList = new ArrayList<>();
		try {
			while (result.next()) {
				String firstName = result.getString("first_name");
				String lastName = result.getString("last_name");
				String address = result.getString("address");
				String city = result.getString("city");
				String state = result.getString("state");
				int zip = result.getInt("zip");
				long phoneNo = result.getLong("phone");
				String email = result.getString("email");
				String addressBookType = result.getString("address_book_type");
				String addressBookName = result.getString("address_book_name");
				contactList.add(new PersonInfo(firstName, lastName, address, city, state, zip, phoneNo, email,
						addressBookName, addressBookType));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	public int updateEmployeeData(String name, String address) {
		return this.updateContactDataUsingPreparedStatement(name, address);
	}

	private int updateContactDataUsingPreparedStatement(String first_name, String address) {
		try (Connection connection = addressBookDBService.getConnection();) {
			String sql = "UPDATE address_book SET address=? WHERE first_name=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, address);
			preparedStatement.setString(2, first_name);
			int status = preparedStatement.executeUpdate();
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<PersonInfo> getcontactData(String name) {
		List<PersonInfo> contactList = null;
		if (this.ContactDataStatement == null)
			this.prepareStatementForContactData();
		try {
			ContactDataStatement.setString(1, name);
			ResultSet resultSet = ContactDataStatement.executeQuery();
			contactList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	private void prepareStatementForContactData() {
		try {
			Connection connection = addressBookDBService.getConnection();
			String sql = "SELECT * FROM address_book WHERE first_name=?; ";
			ContactDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<PersonInfo> getContactForDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT * FROM address_book WHERE date between '%s' AND '%s'; ",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getContactDetailsUsingSqlQuery(sql);
	}

	public Map<String, Integer> getContactByCity() {
		String sql = "SELECT city, count(first_name) as count FROM address_book GROUP BY city;";
		Map<String, Integer> contactByCityMap = new HashMap<>();
		try (Connection connection = addressBookDBService.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String city = result.getString("city");
				Integer count = result.getInt("count");
				contactByCityMap.put(city, count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactByCityMap;
	}

	public PersonInfo addContact(String firstName, String lastName, String address, String city, String state, int zip,
			long phoneNumber, String email, String addressBookName, String addressBookType, LocalDate date) {
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO address_book VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
					firstName, lastName, address, city, state, zip, phoneNumber, email, addressBookName,
					addressBookType, date);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
			}
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return new PersonInfo(firstName, lastName, address, city, state, zip, phoneNumber, email, addressBookName,
				addressBookType, date);
	}
}