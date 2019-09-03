package org.vaadin.klaudeta;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;

/**
 * Pagination component based on lit-pagination webcomponent.
 * 
 * @author klau
 *
 */
@Tag("lit-pagination")
@JsModule("lit-pagination/lit-pagination.js")
@NpmPackage(value = "lit-pagination", version = "^1.0.0")
public class LitPagination extends Component implements LitPaginationModel{


	/**
	 * Default constructor creating an instance of the PlutoniumPagination using
	 * default values.
	 */
	public LitPagination() {
		this.setTotal(2);
		setPageSize(1);
		setSize(1);
	}


	/**
	 * Gets the size of the page, that is number of elements a page could display.
	 * 
	 * @return
	 */
	public int getPageSize() {
		return getLimit();
	}

	/**
	 * Sets the page size, that is number of items a page could display.
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		setLimit(pageSize);
	}

	/**
	 * Sets the count of the pages displayed before or after the current page.
	 * 
	 * @param size
	 */
	public void setPaginatorSize(int size) {
		setSize(size);
	}

	public void refresh() {
		this.getElement().executeJs("$0.observePageCount($1,$2,$3)",this, this.getPage(), this.getPageSize(),
				this.getTotal());
	}
	/**
	 * Adds a ComponentEventListener to be notified with a PageChangeEvent each time
	 * the selected page changes.
	 * 
	 * @param listener to be added
	 * 
	 * @return registration to unregister the listener from the component
	 */
	protected Registration addPageChangeListener(ComponentEventListener<PageChangeEvent> listener) {
		return super.addListener(PageChangeEvent.class, listener);
	}

	/**
	 * A PageChangeEvent specialized class to be fired each time the selected page
	 * on the paginator changes.
	 * 
	 * @author klau
	 *
	 */
	@DomEvent("page-change")
	public static class PageChangeEvent extends ComponentEvent<LitPagination> {
		private final int newPage;
		private final int oldPage;

		public PageChangeEvent(LitPagination source, boolean fromClient,
							   @EventData("event.detail.newPage") int newPage, @EventData("event.detail.oldPage") int oldPage) {
			super(source, fromClient);
			this.newPage = newPage;
			this.oldPage = oldPage;
		}

		/**
		 * Returns the new selected page.
		 * 
		 * @return based 1 index of the selected page
		 */
		public int getNewPage() {
			return newPage;
		}

		/**
		 * Returns the previously selected page before it was changed.
		 * 
		 * @return based 1 index of the previously selected page
		 */
		public int getOldPage() {
			return oldPage;
		}
	}
}
