package lms.code;

/**
 * Thông tin một test kèm bản xem trước nội dung (CodeTestRepository.findInfos): test nhỏ (≤ PREVIEW_FULL byte)
 * có đủ nội dung, test lớn chỉ có PREVIEW_CHARS ký tự đầu.
 */
public record CodeTestInfo(Long id, String publicId, Long problemId, String name, int position, boolean sample,
        long inputSize, long expectedSize, String input, String expected) {

    /** Bằng CodeViews.INLINE_LIMIT; hằng số trong câu JPQL phải là literal. */
    public static final int PREVIEW_FULL = 8192;
    public static final int PREVIEW_CHARS = 300;

    public boolean inputTruncated() {
        return inputSize > PREVIEW_FULL;
    }

    public boolean expectedTruncated() {
        return expectedSize > PREVIEW_FULL;
    }

    public long size() {
        return inputSize + expectedSize;
    }
}
