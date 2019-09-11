package org.vaadin.klaudeta;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.function.SerializableComparator;

/**
 * Grid component where scrolling feature is replaced with a pagination
 * component.
 * 
 * @author klau
 *
 * @param <T>
 */
public class PaginatedGrid<T> extends Grid<T> {

	private PlutoniumPagination paginaton;

	private DataProvider<T, ?> dataProvider;

	public PaginatedGrid() {
		paginaton = new PlutoniumPagination();
		this.dataProvider = super.getDataProvider();
		this.setHeightByRows(true);
		paginaton.addPageChangeListener(e -> doCalcs(e.getNewPage()));
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {

		super.onAttach(attachEvent);
		getParent().ifPresent(p -> {

			int indexOfChild = p.getElement().indexOfChild(this.getElement());
			Span wrapper = new Span(paginaton);
			wrapper.getElement().getStyle().set("width", "100%");
			p.getElement().insertChild(indexOfChild + 1, wrapper.getElement());
		});

		doCalcs(0);
	}

	private void doCalcs(int newPage) {
		int offset = newPage > 0 ? (newPage - 1) * this.getPageSize() : 0;

		InnerQuery query = new InnerQuery<>(offset);

		paginaton.setTotal(dataProvider.size(query));

		super.setDataProvider(DataProvider.fromStream(dataProvider.fetch(query)));
	}

	private void refreshPaginator(){
		if (paginaton != null) {
			paginaton.setPageSize(getPageSize());
			paginaton.setPage(1);
			if(dataProvider != null){
				doCalcs(paginaton.getPage());
			}
			paginaton.refresh();
		}
	}
	@Override
	public void setPageSize(int pageSize) {
		super.setPageSize(pageSize);
		refreshPaginator();

	}

	public void setPage(int page) {
		paginaton.setPage(page);
	}

	public int getPage(){
		return paginaton.getPage();
	}

	@Override
	public void setHeightByRows(boolean heightByRows) {
		super.setHeightByRows(true);
	}

	/**
	 * Sets the count of the pages displayed before or after the current page.
	 * 
	 * @param size
	 */
	public void setPaginatorSize(int size) {
		paginaton.setPage(1);
		paginaton.setPaginatorSize(size);
		paginaton.refresh();
	}

	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		this.dataProvider = dataProvider;
		refreshPaginator();

	}

	/**
	 * Adds a ComponentEventListener to be notified with a PageChangeEvent each time
	 * the selected page changes.
	 *
	 * @param listener to be added
	 *
	 * @return registration to unregister the listener from the component
	 */
	protected Registration addPageChangeListener(ComponentEventListener<PlutoniumPagination.PageChangeEvent> listener) {
		return paginaton.addPageChangeListener(listener);
	}

	private class InnerQuery<F> extends Query<T, F> {

		InnerQuery() {
			this(0);
		}

		InnerQuery(int offset) {
			super(offset, getPageSize(), getDataCommunicator().getBackEndSorting(),
					getDataCommunicator().getInMemorySorting(), null);
		}

		InnerQuery(int offset, List<QuerySortOrder> sortOrders, SerializableComparator<T> serializableComparator) {
			super(offset, getPageSize(), sortOrders, serializableComparator, null);
		}


	}

}
