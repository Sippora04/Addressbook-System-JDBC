package AB.System;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookService {

	private List<PersonInfo> contactList;
	private Map<String, Integer> contactByCity;
	private AddressBookDBService addressBookDBService;

	public AddressBookService(List<PersonInfo> contactList) {
		this();
		this.contactList = contactList;
	}

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public List<PersonInfo> readContactData() {
		this.contactList = addressBookDBService.readData();
		return contactList;
	}

	public void updateContactDetails(String name, String address) {
		int result = addressBookDBService.updateEmployeeData(name, address);
		if (result == 0)
			return;
		PersonInfo personInfo = this.getContactData(name);
		if (personInfo != null)
			personInfo.address = address;
	}

	private PersonInfo getContactData(String name) {
		return this.contactList.stream().filter(contact -> contact.firstName.equals(name)).findFirst().orElse(null);
	}

	public boolean checkConatctDetailsInSyncWithDB(String name) {
		List<PersonInfo> contactList = addressBookDBService.getcontactData(name);
		return contactList.get(0).equals(getContactData(name));
	}

	public List<PersonInfo> readContactDataForDateRange(LocalDate startDate, LocalDate endDate) {
		this.contactList = addressBookDBService.getContactForDateRange(startDate, endDate);
		return contactList;
	}

	public Map<String, Integer> readContactByCityOrState() {
		this.contactByCity = addressBookDBService.getContactByCity();
		return contactByCity;
	}

	public void addContactToAddressBook(String firstName, String lastName, String address, String city, String state,
			int zip, long phoneNumber, String email, String addressBookName, String addressBookType, LocalDate date) {
		contactList.add(addressBookDBService.addContact(firstName, lastName, address, city, state, zip, phoneNumber,
				email, addressBookName, addressBookType, date));
	}

	public void addEmployeeToPayrollWithThreads(List<PersonInfo> contactList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		contactList.forEach(personInfo -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(personInfo.hashCode(), false);
				System.out.println("Contact Being Added:" + Thread.currentThread().getName());
				try {
					this.addContactToAddressBook(personInfo.firstName, personInfo.lastName, personInfo.address,
							personInfo.city, personInfo.state, personInfo.zip, personInfo.phone, personInfo.email,
							personInfo.addressBookName, personInfo.addressBookType, personInfo.date);
				} catch (Exception e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(personInfo.hashCode(), true);
				System.out.println("Contact added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, personInfo.firstName);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("" + this.contactList);
	}
}