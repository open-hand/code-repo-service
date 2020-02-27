package io.choerodon.core.domain;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 分页查询对象客户化
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Page<E> extends AbstractList<E> {
	private int totalPages;
	private long totalElements;
	private int numberOfElements;
	private int size;
	private int number;
	private List<E> content;
	private List<E> list;

	public Page() {
		content = new ArrayList<>();
	}

	/**
	 * 分页封装对象构造函数
	 *
	 * @param content  content
	 * @param pageInfo pageInfo
	 * @param total    total
	 */
	public Page(List<E> content, PageInfo pageInfo, long total) {
		this.content = content;
		//当前页，猪齿鱼前端是从1开始的 为了兼容 +1
		this.number = pageInfo.getPage()+1;
		this.size = pageInfo.getSize();
		this.totalElements = total;
		this.totalPages = (int) (total - 1) / size + 1;
		this.numberOfElements = content.size();

		this.list = content;

	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<E> getContent() {
		return content;
	}

	public void setContent(List<E> content) {
		this.content = content;
		this.list = content;
	}

	@Override
	public E get(int i) {
		return content.get(i);
	}

	@Override
	public int size() {
		return content.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		Page<?> page = (Page<?>) o;

		if (totalPages != page.totalPages) {
			return false;
		}
		if (totalElements != page.totalElements) {
			return false;
		}
		if (numberOfElements != page.numberOfElements) {
			return false;
		}
		if (size != page.size) {
			return false;
		}
		if (number != page.number) {
			return false;
		}
		return content != null ? content.equals(page.content) : page.content == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + totalPages;
		result = 31 * result + (int) (totalElements ^ (totalElements >>> 32));
		result = 31 * result + numberOfElements;
		result = 31 * result + size;
		result = 31 * result + number;
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}
}
