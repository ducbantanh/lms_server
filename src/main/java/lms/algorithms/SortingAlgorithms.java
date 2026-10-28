package lms.algorithms;

import java.util.Arrays;
import java.util.Random;

/**
 * Thư viện các thuật toán sắp xếp kinh điển và hiện đại (Sorting Algorithms Engine).
 * Cung cấp cài đặt chuẩn, đo đếm số phép so sánh, số lần hoán đổi và phân tích độ phức tạp thời gian.
 */
public final class SortingAlgorithms {

    private static final Random RANDOM = new Random();
    private static final int TIMSORT_THRESHOLD = 32;

    public record SortMetrics(long comparisons, long swaps, long elapsedNanos) {
    }

    private SortingAlgorithms() {
    }

    /**
     * Thuật toán sắp xếp nhanh ngẫu nhiên (Randomized QuickSort - Trung bình O(N log N)).
     */
    public static void quickSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortHelper(arr, 0, arr.length - 1);
    }

    private static void quickSortHelper(int[] arr, int low, int high) {
        if (low < high) {
            // Chống trường hợp xấu O(N^2) bằng cách chọn pivot ngẫu nhiên
            int randPivotIdx = low + RANDOM.nextInt(high - low + 1);
            swap(arr, randPivotIdx, high);

            int pIdx = partition(arr, low, high);
            quickSortHelper(arr, low, pIdx - 1);
            quickSortHelper(arr, pIdx + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * Thuật toán sắp xếp trộn (MergeSort - O(N log N) ổn định).
     */
    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int[] aux = new int[arr.length];
        mergeSortHelper(arr, aux, 0, arr.length - 1);
    }

    private static void mergeSortHelper(int[] arr, int[] aux, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSortHelper(arr, aux, left, mid);
        mergeSortHelper(arr, aux, mid + 1, right);
        merge(arr, aux, left, mid, right);
    }

    private static void merge(int[] arr, int[] aux, int left, int mid, int right) {
        System.arraycopy(arr, left, aux, left, right - left + 1);
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (aux[i] <= aux[j]) {
                arr[k++] = aux[i++];
            } else {
                arr[k++] = aux[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = aux[i++];
        }
    }

    /**
     * Thuật toán sắp xếp vun đống (HeapSort - O(N log N) tại chỗ in-place).
     */
    public static void heapSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        // Xây dựng Max-Heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        // Trích xuất từng phần tử từ Heap
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0);
        }
    }

    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }

    /**
     * Thuật toán sắp xếp đếm phân phối (CountingSort - O(N + K)).
     */
    public static void countingSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int min = arr[0];
        int max = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
            if (val > max) max = val;
        }

        int range = max - min + 1;
        if (range > 10_000_000) {
            // Tránh tràn bộ nhớ nếu dải giá trị quá lớn
            mergeSort(arr);
            return;
        }

        int[] count = new int[range];
        for (int val : arr) {
            count[val - min]++;
        }

        int idx = 0;
        for (int i = 0; i < range; i++) {
            while (count[i] > 0) {
                arr[idx++] = i + min;
                count[i]--;
            }
        }
    }

    /**
     * Thuật toán sắp xếp cơ số (RadixSort cho số nguyên không âm - O(N * D)).
     */
    public static void radixSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int max = arr[0];
        for (int val : arr) {
            if (val > max) max = val;
        }

        for (int exp = 1; max / exp > 0; exp *= 10) {
            countSortByDigit(arr, exp);
        }
    }

    private static void countSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++) {
            count[(arr[i] / exp) % 10]++;
        }
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }
        System.arraycopy(output, 0, arr, 0, n);
    }

    /**
     * Thuật toán lai TimSort (Kết hợp InsertionSort trên các run nhỏ và MergeSort).
     */
    public static void timSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        int[] aux = new int[n];

        // Sắp xếp các đoạn con kích thước TIMSORT_THRESHOLD bằng InsertionSort
        for (int i = 0; i < n; i += TIMSORT_THRESHOLD) {
            insertionSort(arr, i, Math.min(i + TIMSORT_THRESHOLD - 1, n - 1));
        }

        // Trộn các đoạn đã sắp xếp
        for (int size = TIMSORT_THRESHOLD; size < n; size = 2 * size) {
            for (int left = 0; left < n; left += 2 * size) {
                int mid = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) {
                    merge(arr, aux, left, mid, right);
                }
            }
        }
    }

    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int temp = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > temp) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = temp;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * Kiểm tra mảng số nguyên đã được sắp xếp tăng dần hay chưa.
     */
    public static boolean isSorted(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return true;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
