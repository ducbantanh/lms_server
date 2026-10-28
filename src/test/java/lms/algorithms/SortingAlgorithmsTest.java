package lms.algorithms;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SortingAlgorithmsTest {

    private final Random random = new Random(42);

    private int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(10000) - 5000;
        }
        return arr;
    }

    @Test
    @DisplayName("quickSort sắp xếp chính xác mảng số nguyên ngẫu nhiên")
    void testQuickSort() {
        int[] arr = generateRandomArray(200);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        SortingAlgorithms.quickSort(arr);
        assertThat(arr).isEqualTo(expected);
        assertThat(SortingAlgorithms.isSorted(arr)).isTrue();
    }

    @Test
    @DisplayName("mergeSort sắp xếp chính xác mảng số nguyên")
    void testMergeSort() {
        int[] arr = generateRandomArray(200);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        SortingAlgorithms.mergeSort(arr);
        assertThat(arr).isEqualTo(expected);
        assertThat(SortingAlgorithms.isSorted(arr)).isTrue();
    }

    @Test
    @DisplayName("heapSort sắp xếp đúng tại chỗ in-place")
    void testHeapSort() {
        int[] arr = generateRandomArray(200);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        SortingAlgorithms.heapSort(arr);
        assertThat(arr).isEqualTo(expected);
        assertThat(SortingAlgorithms.isSorted(arr)).isTrue();
    }

    @Test
    @DisplayName("countingSort sắp xếp đúng các phần tử trong phạm vi")
    void testCountingSort() {
        int[] arr = new int[] { 5, 2, 8, 2, 1, 9, 3, 5, 4 };
        SortingAlgorithms.countingSort(arr);
        assertThat(SortingAlgorithms.isSorted(arr)).isTrue();
    }

    @Test
    @DisplayName("radixSort sắp xếp chính xác các số nguyên không âm")
    void testRadixSort() {
        int[] arr = new int[] { 170, 45, 75, 90, 802, 24, 2, 66 };
        SortingAlgorithms.radixSort(arr);
        assertThat(arr).containsExactly(2, 24, 45, 66, 75, 90, 170, 802);
    }

    @Test
    @DisplayName("timSort sắp xếp chính xác mảng kích thước lớn")
    void testTimSort() {
        int[] arr = generateRandomArray(150);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        SortingAlgorithms.timSort(arr);
        assertThat(arr).isEqualTo(expected);
    }
}
