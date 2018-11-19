package org.vaadin.klaudeta;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * Pagination component based on plutonium-pagination webcomponent.
 * 
 * @author klau
 *
 */
@Tag("plutonium-pagination")
@HtmlImport("bower_components/plutonium-pagination/plutonium-pagination.html")
public class PlutoniumPagination extends PolymerTemplate<PlutoniumPagination.Model> {

	public interface Model extends TemplateModel {

		/**
		 * Returns the selected page on the paginator.
		 * 
		 * @return page
		 */
		int getPage();

		/**
		 * Selects the page on the paginator.
		 * 
		 * @param page to select
		 */
		void setPage(int page);

		/**
		 * Returns the total number of items.
		 * 
		 * @return total
		 */
		int getTotal();

		/**
		 * Sets the total number of items from which number of pages gets calculated.
		 * 
		 * @param total
		 */
		void setTotal(int total);


		/**
		 * Sets the max number of items a page should display in order to calculated the
		 * number of pages.
		 * 
		 * @param limit
		 */
		void setLimit(int limit);

		/**
		 * Gets the actual number of items set to be displayed on for each page.
		 * 
		 * @return
		 */
		int getLimit();

		/**
		 * Sets the count of the pages displayed before or after the current page.
		 * 
		 * @param size
		 */
		void setSize(int size);

	}

	/**
	 * Default constructor creating an instance of the PlutoniumPagination using
	 * default values.
	 */
	public PlutoniumPagination() {
		this.setTotal(2);
		setPageSize(1);
		getModel().setSize(1);
	}

	/**
	 * Returns the selected page on the paginator.
	 * 
	 * @return page
	 */
	public int getPage() {
		return getModel().getPage();
	}

	/**
	 * Sets the page to be selected in the paginator.
	 * 
	 * @param page
	 */
	public void setPage(int page) {
		getModel().setPage(page);
		this.refresh();
	}

	/**
	 * Sets the total number of items to be displayed by pages in the paginator.
	 * 
	 * @param total
	 */
	public void setTotal(int total) {
		getModel().setTotal(total);
	}

	/**
	 * Gets the size of the page, that is number of elements a page could display.
	 * 
	 * @return
	 */
	public int getPageSize() {
		return getModel().getLimit();
	}

	/**
	 * Sets the page size, that is number of items a page could display.
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		getModel().setLimit(pageSize);
	}

	/**
	 * Sets the count of the pages displayed before or after the current page.
	 * 
	 * @param size
	 */
	public void setPaginatorSize(int size) {
		getModel().setSize(size);
	}

	public void refresh() {
		this.getElement().callFunction("observePageCount", this.getPage(), this.getPageSize(),
				this.getModel().getTotal());
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
	@DomEvent("pageChange")
	public static class PageChangeEvent extends ComponentEvent<PlutoniumPagination> {
		private final int newPage;
		private final int oldPage;

		public PageChangeEvent(PlutoniumPagination source, boolean fromClient,
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
