package com;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AddressBookService {
	public AddressBookJDBCService addressbookJDBCService;
	private List<AddressBookData> addressBookDataList;

	public AddressBookService() {
		this.addressbookJDBCService = AddressBookJDBCService.getInstance();
	}

	public List<AddressBookData> readAddressBookData() throws AddressBookException {
		return this.addressbookJDBCService.readData();
	}

	public void updateContactDetails(String name, String state, int zip) throws AddressBookException {
		int result = new AddressBookJDBCService().updateAddressBookDataUsingPreparedStatement(state, zip, name);
		if (result == 0)
			return;
		AddressBookData addressBookData = this.getAddressBookData(name);
		if (addressBookData != null) {
			addressBookData.setZip(zip);
			addressBookData.setState(state);
		}
	}

	public AddressBookData getAddressBookData(String name) {
		return this.addressBookDataList.stream()
				.filter(addressBookDataListObject -> addressBookDataListObject.getFirst_name().equals(name)).findFirst()
				.orElse(null);
	}

	public boolean checkAddressBookInSyncWithDB(String name) throws AddressBookException {
		List<AddressBookData> addressBookDataList = new AddressBookJDBCService().getAddressBookDataFromDB(name);
		return addressBookDataList.get(0).equals(getAddressBookData(name));
	}

	public List<AddressBookData> getAddressBookDataByStartDate(LocalDate startDate, LocalDate endDate)
			throws AddressBookException {
		return this.addressbookJDBCService.getAdressBookDataByStartingDate(startDate, endDate);
	}

	public Map<String, Integer> readContactByCityOrState() throws AddressBookException {
		return this.addressbookJDBCService.getContactsByCityOrState();

	}

	public void addContactToDatabase(String firstName, String lastName, String address, String city, String state,
			int zip, long phone, String email, String type) throws AddressBookException {
		addressBookDataList.add(
				addressbookJDBCService.addContact(firstName, lastName, address, city, state, zip, phone, email, type));
	}

}