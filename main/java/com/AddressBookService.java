package com;

import java.util.List;

public class AddressBookService {
	public AddressBookJDBCService addressbookJDBCService;

	public AddressBookService() {
		this.addressbookJDBCService = new AddressBookJDBCService();
	}

	public List<AddressBookData> readAddressBookData() throws AddressBookException {
		return this.addressbookJDBCService.readData();
	}
}