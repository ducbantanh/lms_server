package lms.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tiện ích xử lý danh sách và tập hợp dữ liệu lớn (Batch Partitioning & Pagination).
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Chia nhỏ danh sách lớn thành các khối (chunks / batches) với kích thước tối đa chỉ định.
     */
    public static <T> List<List<T>> partition(List<T> list, int chunkSize) {
        if (list == null || list.isEmpty() || chunkSize <= 0) {
            return Collections.emptyList();
        }

        List<List<T>> partitions = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += chunkSize) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(size, i + chunkSize))));
        }
        return partitions;
    }

    /**
     * Cắt danh sách an toàn theo trang (In-Memory Pagination Slicing).
     */
    public static <T> List<T> slicePage(List<T> list, int pageIndex, int pageSize) {
        if (list == null || list.isEmpty() || pageSize <= 0 || pageIndex < 0) {
            return Collections.emptyList();
        }
        int fromIndex = pageIndex * pageSize;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }
}
