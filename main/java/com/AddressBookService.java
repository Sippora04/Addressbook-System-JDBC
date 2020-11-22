package com;

import java.time.LocalDate;
import java.util.List;

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

	AddressBookData getAddressBookData(String name) {
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
}