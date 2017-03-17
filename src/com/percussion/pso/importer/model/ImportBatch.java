package com.percussion.pso.importer.model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.percussion.pso.importer.manager.ItemManager;
import com.percussion.pso.rxws.item.RxWsContext;

@XmlRootElement(name = "importer")
public class ImportBatch  implements Iterable<ImportItem>{

	private List<ImportItem> items = new ArrayList<ImportItem>();
	private List<ImportError> errors = new ArrayList<ImportError>();
	
	private ItemManager itemManager;

	
	public ImportBatch() {
	}
	

	@XmlElement(name="item")
	public List<ImportItem> getItems() {
		return items;
	}

	public void setItems(List<ImportItem> items) {
		this.items = items;
	}

	public boolean add(ImportItem o) {
		return items.add(o);
	}

	public void add(int index, ImportItem element) {
		items.add(index, element);
	}

	public boolean addAll(Collection<? extends ImportItem> c) {
		return items.addAll(c);
	}

	public boolean addAll(ImportBatch c) {
		return items.addAll(c.getItems());
	}
	public boolean addAll(int index, Collection<? extends ImportItem> c) {
		return items.addAll(index, c);
	}

	public void clear() {
		items.clear();
	}

	public boolean contains(Object o) {
		return items.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	public ImportItem get(int index) {
		return items.get(index);
	}

	public int indexOf(Object o) {
		return items.indexOf(o);
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public Iterator<ImportItem> iterator() {
		return items.iterator();
	}

	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ListIterator<ImportItem> listIterator() {
		return items.listIterator();
	}

	public ListIterator<ImportItem> listIterator(int index) {
		return items.listIterator(index);
	}

	public boolean remove(Object o) {
		return items.remove(o);
	}

	public ImportItem remove(int index) {
		return items.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		return items.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return items.removeAll(c);
	}

	public ImportItem set(int index, ImportItem element) {
		return items.set(index, element);
	}

	public int size() {
		return items.size();
	}

	public List<ImportItem> subList(int fromIndex, int toIndex) {
		return items.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return items.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return items.toArray(a);
	}

	
	@XmlElementWrapper(name="errors")
	@XmlElement(name="error")
	public List<ImportError> getErrors() {
		return errors;
	}

	public void setErrors(List<ImportError> errors) {
		this.errors = errors;
	}
	public void addError(String error) {
		if(this.errors == null) this.errors = new ArrayList<ImportError>();
		this.errors.add(new ImportError(error));
	}
	public void addError(String error, Exception e) {
		if(this.errors == null) this.errors = new ArrayList<ImportError>();
		this.errors.add(new ImportError(error,e));
	}
	public void clearErrors(String error) {
		this.errors = null;
	}
	@XmlTransient
	public ItemManager getItemManager() {
		return itemManager;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}
	


}
