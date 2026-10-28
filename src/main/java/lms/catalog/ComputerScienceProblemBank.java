package lms.catalog;

import java.util.*;

/**
 * Ngân hàng bài toán Khoa học máy tính & Thuật toán kinh điển (120 bài toán chọn lọc).
 * Phục vụ cho hệ thống Luyện tập Code Judge, Đánh giá năng lực thuật toán và Phỏng vấn kỹ thuật.
 */
public final class ComputerScienceProblemBank {

    public record TestCase(
            String input,
            String expectedOutput,
            boolean isHidden
    ) {}

    public record ProblemItem(
            String id,
            String title,
            String category,
            String difficulty,
            double acceptanceRate,
            String description,
            List<String> hints,
            String starterJava,
            String starterPython,
            String starterCpp,
            List<TestCase> testCases
    ) {}

    private static final Map<String, ProblemItem> PROBLEMS = new LinkedHashMap<>();

    static {
        registerProblem(new ProblemItem(
                "PROB-001",
                "Two Sum",
                "Hash Table",
                "EASY",
                49.5,
                "Cho một mảng số nguyên nums và một số nguyên target, hãy tìm chỉ số của hai số sao cho tổng của chúng đúng bằng target.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Two Sum.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Hash Table để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Two Sum.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Hash Table\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Two Sum.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Hash Table\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Two Sum\n        // Khuyen khich su dung cau truc du lieu Hash Table\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-002",
                "Valid Parentheses",
                "Stack",
                "EASY",
                40.2,
                "Cho chuỗi chứa các dấu ngoặc, xác định xem chuỗi ngoặc có đóng mở hợp lệ theo đúng thứ tự không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Valid Parentheses.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Stack để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Valid Parentheses.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Stack\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Valid Parentheses.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Stack\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Valid Parentheses\n        // Khuyen khich su dung cau truc du lieu Stack\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-003",
                "Merge Two Sorted Lists",
                "Linked List",
                "EASY",
                62.8,
                "Trộn hai danh sách liên kết đơn đã sắp xếp tăng dần thành một danh sách liên kết mới duy nhất.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Merge Two Sorted Lists.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Linked List để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Merge Two Sorted Lists.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Linked List\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Merge Two Sorted Lists.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Linked List\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Merge Two Sorted Lists\n        // Khuyen khich su dung cau truc du lieu Linked List\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-004",
                "Maximum Subarray Sum",
                "Dynamic Programming",
                "MEDIUM",
                50.1,
                "Tìm dãy con liên tiếp có tổng lớn nhất trong một mảng số nguyên (Thuật toán Kadane).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Maximum Subarray Sum.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Maximum Subarray Sum.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Maximum Subarray Sum.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Maximum Subarray Sum\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-005",
                "Longest Substring Without Repeating",
                "Sliding Window",
                "MEDIUM",
                34.0,
                "Tìm độ dài chuỗi con liên tiếp dài nhất không chứa bất kỳ ký tự nào lặp lại.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Longest Substring Without Repeating.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Sliding Window để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Longest Substring Without Repeating.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Sliding Window\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Longest Substring Without Repeating.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Sliding Window\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Longest Substring Without Repeating\n        // Khuyen khich su dung cau truc du lieu Sliding Window\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-006",
                "Three Sum",
                "Two Pointers",
                "MEDIUM",
                32.7,
                "Tìm tất cả các bộ ba số duy nhất trong mảng có tổng đúng bằng 0.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Three Sum.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Two Pointers để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Three Sum.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Two Pointers\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Three Sum.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Two Pointers\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Three Sum\n        // Khuyen khich su dung cau truc du lieu Two Pointers\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-007",
                "Container With Most Water",
                "Two Pointers",
                "MEDIUM",
                54.3,
                "Tìm hai cột trong mảng chiều cao tạo với trục hoành một bình chứa được thể tích nước lớn nhất.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Container With Most Water.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Two Pointers để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Container With Most Water.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Two Pointers\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Container With Most Water.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Two Pointers\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Container With Most Water\n        // Khuyen khich su dung cau truc du lieu Two Pointers\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-008",
                "Trapping Rain Water",
                "Two Pointers",
                "HARD",
                59.4,
                "Tính toán thể tích nước mưa tối đa có thể giữ lại được sau cơn mưa trên bản đồ độ cao.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Trapping Rain Water.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Two Pointers để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Trapping Rain Water.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Two Pointers\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Trapping Rain Water.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Two Pointers\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Trapping Rain Water\n        // Khuyen khich su dung cau truc du lieu Two Pointers\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-009",
                "Binary Search",
                "Binary Search",
                "EASY",
                56.1,
                "Tìm kiếm phần tử target trong mảng số nguyên đã sắp xếp tăng dần trong O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Binary Search.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Binary Search.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Binary Search.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Binary Search\n        // Khuyen khich su dung cau truc du lieu Binary Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-010",
                "Search in Rotated Sorted Array",
                "Binary Search",
                "MEDIUM",
                39.8,
                "Tìm kiếm phần tử target trong mảng số nguyên đã sắp xếp nhưng bị xoay vòng tại một pivot.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Search in Rotated Sorted Array.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Search in Rotated Sorted Array.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Search in Rotated Sorted Array.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Search in Rotated Sorted Array\n        // Khuyen khich su dung cau truc du lieu Binary Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-011",
                "Find Minimum in Rotated Sorted Array",
                "Binary Search",
                "MEDIUM",
                49.2,
                "Tìm phần tử có giá trị nhỏ nhất trong mảng số nguyên tăng dần bị xoay vòng.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Find Minimum in Rotated Sorted Array.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Find Minimum in Rotated Sorted Array.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Find Minimum in Rotated Sorted Array.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Find Minimum in Rotated Sorted Array\n        // Khuyen khich su dung cau truc du lieu Binary Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-012",
                "Kth Largest Element in Array",
                "Heap",
                "MEDIUM",
                66.5,
                "Tìm phần tử lớn thứ k trong mảng số nguyên chưa qua sắp xếp mà không cần sort toàn bộ mảng.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Kth Largest Element in Array.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Heap để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Kth Largest Element in Array.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Heap\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Kth Largest Element in Array.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Heap\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Kth Largest Element in Array\n        // Khuyen khich su dung cau truc du lieu Heap\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-013",
                "Top K Frequent Elements",
                "Heap",
                "MEDIUM",
                63.2,
                "Tìm k phần tử xuất hiện nhiều nhất trong mảng số nguyên bằng Bucket Sort hoặc Min-Heap.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Top K Frequent Elements.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Heap để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Top K Frequent Elements.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Heap\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Top K Frequent Elements.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Heap\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Top K Frequent Elements\n        // Khuyen khich su dung cau truc du lieu Heap\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-014",
                "Find Median from Data Stream",
                "Heap",
                "HARD",
                51.3,
                "Thiết kế cấu trúc dữ liệu hỗ trợ chèn số thực và tìm giá trị trung vị trong O(1).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Find Median from Data Stream.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Heap để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Find Median from Data Stream.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Heap\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Find Median from Data Stream.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Heap\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Find Median from Data Stream\n        // Khuyen khich su dung cau truc du lieu Heap\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-015",
                "Merge K Sorted Lists",
                "Heap",
                "HARD",
                50.6,
                "Trộn k danh sách liên kết đã sắp xếp thành một danh sách liên kết duy nhất.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Merge K Sorted Lists.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Heap để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Merge K Sorted Lists.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Heap\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Merge K Sorted Lists.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Heap\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Merge K Sorted Lists\n        // Khuyen khich su dung cau truc du lieu Heap\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-016",
                "Climbing Stairs",
                "Dynamic Programming",
                "EASY",
                52.4,
                "Đếm số cách leo lên đỉnh cầu thang n bậc khi mỗi lần chỉ có thể bước 1 hoặc 2 bậc.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Climbing Stairs.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Climbing Stairs.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Climbing Stairs.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Climbing Stairs\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-017",
                "Coin Change",
                "Dynamic Programming",
                "MEDIUM",
                42.1,
                "Tìm số lượng đồng xu tối thiểu cần sử dụng để đổi thành số tiền target cho trước.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Coin Change.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Coin Change.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Coin Change.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Coin Change\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-018",
                "Coin Change II",
                "Dynamic Programming",
                "MEDIUM",
                62.0,
                "Tính tổng số cách đổi tiền khác nhau từ các mệnh giá xu cho trước với số lượng không giới hạn.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Coin Change II.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Coin Change II.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Coin Change II.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Coin Change II\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-019",
                "Longest Increasing Subsequence",
                "Dynamic Programming",
                "MEDIUM",
                53.8,
                "Tìm độ dài của dãy con tăng dài nhất trong mảng số nguyên bằng quy hoạch động O(N log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Longest Increasing Subsequence.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Longest Increasing Subsequence.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Longest Increasing Subsequence.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Longest Increasing Subsequence\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-020",
                "Longest Common Subsequence",
                "Dynamic Programming",
                "MEDIUM",
                58.9,
                "Tìm độ dài của chuỗi con chung dài nhất giữa hai chuỗi văn bản text1 và text2.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Longest Common Subsequence.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Longest Common Subsequence.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Longest Common Subsequence.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Longest Common Subsequence\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-021",
                "Edit Distance",
                "Dynamic Programming",
                "HARD",
                55.4,
                "Tính số thao tác chèn, xóa, thay thế ký tự tối thiểu để biến chuỗi s1 thành s2 (Levenshtein Distance).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Edit Distance.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Edit Distance.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Edit Distance.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Edit Distance\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-022",
                "0-1 Knapsack Problem",
                "Dynamic Programming",
                "MEDIUM",
                55.0,
                "Chọn các vật phẩm với trọng lượng w[i] và giá trị v[i] để tối đa hóa giá trị trong ba lô dung lượng W.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán 0-1 Knapsack Problem.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán 0-1 Knapsack Problem.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan 0-1 Knapsack Problem.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho 0-1 Knapsack Problem\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-023",
                "Subset Sum Problem",
                "Dynamic Programming",
                "MEDIUM",
                48.0,
                "Xác định xem có tồn tại tập con nào của mảng có tổng đúng bằng giá trị S hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Subset Sum Problem.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Subset Sum Problem.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Subset Sum Problem.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Subset Sum Problem\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-024",
                "Partition Equal Subset Sum",
                "Dynamic Programming",
                "MEDIUM",
                46.2,
                "Kiểm tra xem mảng số nguyên có thể chia thành hai tập con có tổng bằng nhau hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Partition Equal Subset Sum.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Partition Equal Subset Sum.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Partition Equal Subset Sum.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Partition Equal Subset Sum\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-025",
                "Word Break",
                "Dynamic Programming",
                "MEDIUM",
                45.9,
                "Xác định xem chuỗi s có thể phân tách thành các từ có trong từ điển dict hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Word Break.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Word Break.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Word Break.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Word Break\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-026",
                "Palindrome Partitioning",
                "Backtracking",
                "MEDIUM",
                65.7,
                "Phân hoạch chuỗi s sao cho mọi chuỗi con trong phân hoạch đều là chuỗi đối xứng.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Palindrome Partitioning.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Backtracking để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Palindrome Partitioning.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Backtracking\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Palindrome Partitioning.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Backtracking\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Palindrome Partitioning\n        // Khuyen khich su dung cau truc du lieu Backtracking\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-027",
                "Number of Islands",
                "Graph",
                "MEDIUM",
                57.8,
                "Đếm số lượng hòn đảo được bao quanh bởi nước trên ma trận nhị phân 2D bằng BFS/DFS.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Number of Islands.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Number of Islands.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Number of Islands.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Number of Islands\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-028",
                "Clone Graph",
                "Graph",
                "MEDIUM",
                53.4,
                "Tạo bản sao sâu (Deep Copy) của đồ thị vô hướng liên thông.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Clone Graph.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Clone Graph.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Clone Graph.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Clone Graph\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-029",
                "Course Schedule",
                "Graph",
                "MEDIUM",
                46.0,
                "Xác định xem có thể hoàn thành tất cả môn học với các điều kiện tiên quyết (Thuật toán Kahn / DFS Topological Sort).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Course Schedule.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Course Schedule.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Course Schedule.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Course Schedule\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-030",
                "Course Schedule II",
                "Graph",
                "MEDIUM",
                48.9,
                "Trả về thứ tự các môn học cần đăng ký để hoàn thành toàn bộ chương trình đào tạo.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Course Schedule II.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Course Schedule II.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Course Schedule II.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Course Schedule II\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-031",
                "Rotting Oranges",
                "Breadth-First Search",
                "MEDIUM",
                53.1,
                "Tính số phút tối thiểu để tất cả các quả cam tươi trên lưới bị thối lây lan từ cam thối.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Rotting Oranges.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Breadth-First Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Rotting Oranges.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Breadth-First Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Rotting Oranges.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Breadth-First Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Rotting Oranges\n        // Khuyen khich su dung cau truc du lieu Breadth-First Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-032",
                "Word Ladder",
                "Breadth-First Search",
                "HARD",
                37.5,
                "Tìm số bước biến đổi ngắn nhất từ beginWord thành endWord qua danh sách từ điển.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Word Ladder.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Breadth-First Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Word Ladder.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Breadth-First Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Word Ladder.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Breadth-First Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Word Ladder\n        // Khuyen khich su dung cau truc du lieu Breadth-First Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-033",
                "Word Ladder II",
                "Breadth-First Search",
                "HARD",
                27.8,
                "Tìm tất cả các chuỗi biến đổi ngắn nhất từ từ bắt đầu đến từ kết thúc.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Word Ladder II.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Breadth-First Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Word Ladder II.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Breadth-First Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Word Ladder II.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Breadth-First Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Word Ladder II\n        // Khuyen khich su dung cau truc du lieu Breadth-First Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-034",
                "Pacific Atlantic Water Flow",
                "Depth-First Search",
                "MEDIUM",
                54.6,
                "Tìm các tọa độ trên lưới mà nước mưa có thể chảy ra cả Thái Bình Dương và Đại Tây Dương.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Pacific Atlantic Water Flow.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Depth-First Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Pacific Atlantic Water Flow.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Depth-First Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Pacific Atlantic Water Flow.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Depth-First Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Pacific Atlantic Water Flow\n        // Khuyen khich su dung cau truc du lieu Depth-First Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-035",
                "Surrounded Regions",
                "Depth-First Search",
                "MEDIUM",
                38.0,
                "Bắt tất cả các vùng 'O' bị bao quanh hoàn toàn bởi 'X' trên ma trận 2D.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Surrounded Regions.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Depth-First Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Surrounded Regions.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Depth-First Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Surrounded Regions.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Depth-First Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Surrounded Regions\n        // Khuyen khich su dung cau truc du lieu Depth-First Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-036",
                "Implement Trie Prefix Tree",
                "Trie",
                "MEDIUM",
                63.4,
                "Cài đặt cây tiền tố Trie với các phương thức insert, search và startsWith.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Implement Trie Prefix Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Trie để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Implement Trie Prefix Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Trie\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Implement Trie Prefix Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Trie\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Implement Trie Prefix Tree\n        // Khuyen khich su dung cau truc du lieu Trie\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-037",
                "Design Add and Search Words Data Structure",
                "Trie",
                "MEDIUM",
                44.1,
                "Thiết kế cấu trúc dữ liệu từ điển hỗ trợ tìm kiếm mẫu ký tự đại diện '.'\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Design Add and Search Words Data Structure.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Trie để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Design Add and Search Words Data Structure.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Trie\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Design Add and Search Words Data Structure.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Trie\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Design Add and Search Words Data Structure\n        // Khuyen khich su dung cau truc du lieu Trie\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-038",
                "Word Search II",
                "Trie",
                "HARD",
                36.5,
                "Tìm kiếm tất cả các từ trong từ điển xuất hiện trên lưới ký tự 2D bằng cách kết hợp Trie và Backtracking.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Word Search II.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Trie để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Word Search II.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Trie\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Word Search II.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Trie\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Word Search II\n        // Khuyen khich su dung cau truc du lieu Trie\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-039",
                "Lowest Common Ancestor of Binary Tree",
                "Binary Tree",
                "MEDIUM",
                59.8,
                "Tìm tổ tiên chung gần nhất (LCA) của hai nút p và q trong cây nhị phân.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Lowest Common Ancestor of Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Lowest Common Ancestor of Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Lowest Common Ancestor of Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Lowest Common Ancestor of Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-040",
                "Binary Tree Maximum Path Sum",
                "Binary Tree",
                "HARD",
                39.3,
                "Tìm đường đi có tổng giá trị các nút lớn nhất trong cây nhị phân bất kỳ.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Binary Tree Maximum Path Sum.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Binary Tree Maximum Path Sum.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Binary Tree Maximum Path Sum.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Binary Tree Maximum Path Sum\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-041",
                "Serialize and Deserialize Binary Tree",
                "Binary Tree",
                "HARD",
                55.6,
                "Thiết kế thuật toán tuần tự hóa cây nhị phân thành chuỗi và giải tuần tự hóa ngược lại.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Serialize and Deserialize Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Serialize and Deserialize Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Serialize and Deserialize Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Serialize and Deserialize Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-042",
                "Diameter of Binary Tree",
                "Binary Tree",
                "EASY",
                58.2,
                "Tính đường kính (khoảng cách dài nhất giữa hai nút bất kỳ) của cây nhị phân.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Diameter of Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Diameter of Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Diameter of Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Diameter of Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-043",
                "Balanced Binary Tree",
                "Binary Tree",
                "EASY",
                49.8,
                "Kiểm tra xem cây nhị phân có cân bằng theo chiều cao (chiều cao 2 cây con lệch không quá 1) hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Balanced Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Balanced Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Balanced Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Balanced Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-044",
                "Invert Binary Tree",
                "Binary Tree",
                "EASY",
                75.3,
                "Đảo ngược cây nhị phân (gương lật cây).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Invert Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Invert Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Invert Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Invert Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-045",
                "Validate Binary Search Tree",
                "Binary Search Tree",
                "MEDIUM",
                32.2,
                "Xác thực xem cây nhị phân cho trước có thỏa mãn đầy đủ tính chất của cây BST hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Validate Binary Search Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Validate Binary Search Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Validate Binary Search Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Validate Binary Search Tree\n        // Khuyen khich su dung cau truc du lieu Binary Search Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-046",
                "Kth Smallest Element in BST",
                "Binary Search Tree",
                "MEDIUM",
                70.1,
                "Tìm phần tử nhỏ thứ k trong cây tìm kiếm nhị phân.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Kth Smallest Element in BST.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Kth Smallest Element in BST.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Kth Smallest Element in BST.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Kth Smallest Element in BST\n        // Khuyen khich su dung cau truc du lieu Binary Search Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-047",
                "Construct Binary Tree from Preorder and Inorder",
                "Binary Tree",
                "MEDIUM",
                62.1,
                "Tái tạo lại cây nhị phân ban đầu từ thứ tự duyệt Preorder và Inorder.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Construct Binary Tree from Preorder and Inorder.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Construct Binary Tree from Preorder and Inorder.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Construct Binary Tree from Preorder and Inorder.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Construct Binary Tree from Preorder and Inorder\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-048",
                "Subtree of Another Tree",
                "Binary Tree",
                "EASY",
                46.9,
                "Kiểm tra xem cây subRoot có phải là cây con của cây root hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Subtree of Another Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Subtree of Another Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Subtree of Another Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Subtree of Another Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-049",
                "Binary Tree Level Order Traversal",
                "Binary Tree",
                "MEDIUM",
                65.4,
                "Trả về danh sách các giá trị nút duyệt theo từng tầng từ trên xuống dưới.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Binary Tree Level Order Traversal.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Binary Tree Level Order Traversal.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Binary Tree Level Order Traversal.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Binary Tree Level Order Traversal\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-050",
                "Maximum Depth of Binary Tree",
                "Binary Tree",
                "EASY",
                74.5,
                "Tìm chiều sâu tối đa (chiều cao) của cây nhị phân cho trước.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Maximum Depth of Binary Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Maximum Depth of Binary Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Maximum Depth of Binary Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Maximum Depth of Binary Tree\n        // Khuyen khich su dung cau truc du lieu Binary Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-051",
                "Disjoint Set Union",
                "Union Find",
                "MEDIUM",
                61.2,
                "Cài đặt Disjoint Set Union (DSU) với Path Compression và Union by Rank hỗ trợ find và union trong O(alpha(N)).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Disjoint Set Union.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Union Find để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Disjoint Set Union.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Union Find\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Disjoint Set Union.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Union Find\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Disjoint Set Union\n        // Khuyen khich su dung cau truc du lieu Union Find\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-052",
                "Segment Tree Range Sum Query",
                "Segment Tree",
                "MEDIUM",
                45.8,
                "Cài đặt Segment Tree hỗ trợ Point Update và Range Sum Query trong O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Segment Tree Range Sum Query.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Segment Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Segment Tree Range Sum Query.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Segment Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Segment Tree Range Sum Query.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Segment Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Segment Tree Range Sum Query\n        // Khuyen khich su dung cau truc du lieu Segment Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-053",
                "Segment Tree Lazy Propagation",
                "Segment Tree",
                "HARD",
                38.4,
                "Cài đặt Segment Tree với kỹ thuật Lazy Propagation hỗ trợ Range Update và Range Query trong O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Segment Tree Lazy Propagation.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Segment Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Segment Tree Lazy Propagation.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Segment Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Segment Tree Lazy Propagation.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Segment Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Segment Tree Lazy Propagation\n        // Khuyen khich su dung cau truc du lieu Segment Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-054",
                "Binary Indexed Tree Fenwick",
                "Binary Indexed Tree",
                "MEDIUM",
                52.0,
                "Cài đặt Fenwick Tree hỗ trợ cập nhật đơn điểm và truy vấn tổng tiền tố trong O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Binary Indexed Tree Fenwick.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Indexed Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Binary Indexed Tree Fenwick.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Indexed Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Binary Indexed Tree Fenwick.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Indexed Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Binary Indexed Tree Fenwick\n        // Khuyen khich su dung cau truc du lieu Binary Indexed Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-055",
                "Dijkstra Shortest Path",
                "Graph",
                "MEDIUM",
                58.7,
                "Tìm đường đi ngắn nhất từ đỉnh nguồn đến tất cả các đỉnh còn lại trên đồ thị có trọng số không âm.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Dijkstra Shortest Path.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Dijkstra Shortest Path.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Dijkstra Shortest Path.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Dijkstra Shortest Path\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-056",
                "Bellman Ford Shortest Path",
                "Graph",
                "MEDIUM",
                49.3,
                "Tìm đường đi ngắn nhất từ đỉnh nguồn và phát hiện chu trình âm trên đồ thị có trọng số.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Bellman Ford Shortest Path.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Bellman Ford Shortest Path.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Bellman Ford Shortest Path.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Bellman Ford Shortest Path\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-057",
                "Floyd Warshall All Pairs",
                "Graph",
                "MEDIUM",
                54.1,
                "Tìm đường đi ngắn nhất giữa tất cả các cặp đỉnh trên đồ thị bằng quy hoạch động O(V^3).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Floyd Warshall All Pairs.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Floyd Warshall All Pairs.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Floyd Warshall All Pairs.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Floyd Warshall All Pairs\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-058",
                "Kruskal Minimum Spanning Tree",
                "Graph",
                "MEDIUM",
                60.5,
                "Tìm cây khung nhỏ nhất (MST) của đồ thị vô hướng liên thông bằng thuật toán tham lam Kruskal và DSU.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Kruskal Minimum Spanning Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Kruskal Minimum Spanning Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Kruskal Minimum Spanning Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Kruskal Minimum Spanning Tree\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-059",
                "Prim Minimum Spanning Tree",
                "Graph",
                "MEDIUM",
                57.2,
                "Tìm cây khung nhỏ nhất (MST) của đồ thị vô hướng liên thông bằng thuật toán Prim và Priority Queue.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Prim Minimum Spanning Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Prim Minimum Spanning Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Prim Minimum Spanning Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Prim Minimum Spanning Tree\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-060",
                "Tarjan Strongly Connected Components",
                "Graph",
                "HARD",
                42.6,
                "Tìm các thành phần liên thông mạnh (SCC) trong đồ thị có hướng bằng thuật toán Tarjan một lần DFS.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Tarjan Strongly Connected Components.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Tarjan Strongly Connected Components.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Tarjan Strongly Connected Components.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Tarjan Strongly Connected Components\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-061",
                "Kosaraju Strongly Connected Components",
                "Graph",
                "HARD",
                44.0,
                "Tìm các thành phần liên thông mạnh trong đồ thị có hướng bằng 2 lần DFS trên đồ thị gốc và đồ thị đảo ngược.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Kosaraju Strongly Connected Components.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Kosaraju Strongly Connected Components.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Kosaraju Strongly Connected Components.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Kosaraju Strongly Connected Components\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-062",
                "Edmonds Karp Maximum Flow",
                "Network Flow",
                "HARD",
                39.5,
                "Tìm luồng cực đại trên mạng luồng bằng thuật toán Edmonds-Karp kết hợp đường tăng luồng BFS.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Edmonds Karp Maximum Flow.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Network Flow để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Edmonds Karp Maximum Flow.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Network Flow\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Edmonds Karp Maximum Flow.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Network Flow\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Edmonds Karp Maximum Flow\n        // Khuyen khich su dung cau truc du lieu Network Flow\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-063",
                "Dinic Maximum Flow",
                "Network Flow",
                "HARD",
                36.8,
                "Tìm luồng cực đại trên mạng luồng với hiệu năng O(V^2 E) bằng đồ thị phân tầng Level Graph và Blocking Flow.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Dinic Maximum Flow.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Network Flow để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Dinic Maximum Flow.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Network Flow\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Dinic Maximum Flow.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Network Flow\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Dinic Maximum Flow\n        // Khuyen khich su dung cau truc du lieu Network Flow\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-064",
                "Bipartite Graph Matching Hungarian",
                "Graph",
                "HARD",
                41.2,
                "Tìm cặp ghép cực đại trên đồ thị hai phía có trọng số bằng thuật toán Hungary.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Bipartite Graph Matching Hungarian.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Bipartite Graph Matching Hungarian.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Bipartite Graph Matching Hungarian.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Bipartite Graph Matching Hungarian\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-065",
                "Convex Hull Graham Scan",
                "Computational Geometry",
                "HARD",
                37.1,
                "Tìm bao lồi của tập hợp N điểm trên mặt phẳng 2D bằng thuật toán Graham Scan trong O(N log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Convex Hull Graham Scan.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Computational Geometry để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Convex Hull Graham Scan.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Computational Geometry\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Convex Hull Graham Scan.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Computational Geometry\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Convex Hull Graham Scan\n        // Khuyen khich su dung cau truc du lieu Computational Geometry\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-066",
                "Closest Pair of Points",
                "Divide and Conquer",
                "HARD",
                35.4,
                "Tìm cặp điểm có khoảng cách Euclid nhỏ nhất trong tập N điểm bằng thuật toán chia để trị O(N log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Closest Pair of Points.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Divide and Conquer để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Closest Pair of Points.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Divide and Conquer\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Closest Pair of Points.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Divide and Conquer\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Closest Pair of Points\n        // Khuyen khich su dung cau truc du lieu Divide and Conquer\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-067",
                "Traveling Salesman Bitmask DP",
                "Dynamic Programming",
                "HARD",
                32.1,
                "Giải bài toán người bán hàng chu du qua tất cả N thành phố với chi phí nhỏ nhất bằng Bitmask DP trong O(2^N * N^2).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Traveling Salesman Bitmask DP.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Traveling Salesman Bitmask DP.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Traveling Salesman Bitmask DP.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Traveling Salesman Bitmask DP\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-068",
                "Longest Palindromic Substring Manacher",
                "String",
                "HARD",
                33.5,
                "Tìm chuỗi con đối xứng dài nhất trong chuỗi văn bản bằng thuật toán Manacher tuyến tính O(N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Longest Palindromic Substring Manacher.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Longest Palindromic Substring Manacher.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Longest Palindromic Substring Manacher.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Longest Palindromic Substring Manacher\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-069",
                "Knuth Morris Pratt String Match",
                "String",
                "MEDIUM",
                47.3,
                "Tìm kiếm vị trí xuất hiện của chuỗi mẫu pattern trong text bằng bảng tiền tố Pi của thuật toán KMP trong O(N + M).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Knuth Morris Pratt String Match.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Knuth Morris Pratt String Match.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Knuth Morris Pratt String Match.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Knuth Morris Pratt String Match\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-070",
                "Rabin Karp Rolling Hash",
                "String",
                "MEDIUM",
                45.0,
                "Tìm kiếm chuỗi mẫu trong văn bản bằng hàm băm cuộn Rolling Hash trong O(N + M) thời gian trung bình.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Rabin Karp Rolling Hash.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Rabin Karp Rolling Hash.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Rabin Karp Rolling Hash.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Rabin Karp Rolling Hash\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-071",
                "Z Algorithm Pattern Match",
                "String",
                "MEDIUM",
                48.6,
                "Tính mảng Z-array để tìm kiếm chuỗi mẫu trong văn bản với thời gian tuyến tính O(N + M).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Z Algorithm Pattern Match.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Z Algorithm Pattern Match.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Z Algorithm Pattern Match.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Z Algorithm Pattern Match\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-072",
                "Aho Corasick Multi Pattern",
                "String",
                "HARD",
                31.9,
                "Tìm kiếm đồng thời nhiều chuỗi mẫu trong văn bản lớn bằng cây tiền tố và các liên kết thất bại Failure Links.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Aho Corasick Multi Pattern.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Aho Corasick Multi Pattern.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Aho Corasick Multi Pattern.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Aho Corasick Multi Pattern\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-073",
                "Heavy Light Decomposition",
                "Tree",
                "HARD",
                28.4,
                "Phân rã cây thành các chuỗi nặng-nhẹ hỗ trợ truy vấn và cập nhật trên đường đi cây trong O(log^2 N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Heavy Light Decomposition.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Heavy Light Decomposition.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Heavy Light Decomposition.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Heavy Light Decomposition\n        // Khuyen khich su dung cau truc du lieu Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-074",
                "Lowest Common Ancestor Binary Lifting",
                "Tree",
                "MEDIUM",
                55.2,
                "Tìm tổ tiên chung gần nhất (LCA) của hai nút trên cây tổng quát bằng kỹ thuật Binary Lifting trong O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Lowest Common Ancestor Binary Lifting.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Lowest Common Ancestor Binary Lifting.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Lowest Common Ancestor Binary Lifting.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Lowest Common Ancestor Binary Lifting\n        // Khuyen khich su dung cau truc du lieu Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-075",
                "Treap Cartesian Tree",
                "Data Structure",
                "HARD",
                34.6,
                "Cài đặt cây Treap kết hợp tính chất của Binary Search Tree và Binary Heap bằng trọng số ngẫu nhiên.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Treap Cartesian Tree.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Data Structure để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Treap Cartesian Tree.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Data Structure\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Treap Cartesian Tree.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Data Structure\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Treap Cartesian Tree\n        // Khuyen khich su dung cau truc du lieu Data Structure\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-076",
                "Red Black Tree Insertion",
                "Data Structure",
                "HARD",
                29.8,
                "Cài đặt phép chèn và cân bằng lại cây đỏ đen (Red-Black Tree) đảm bảo chiều cao tối đa 2 * log(N + 1).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Red Black Tree Insertion.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Data Structure để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Red Black Tree Insertion.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Data Structure\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Red Black Tree Insertion.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Data Structure\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Red Black Tree Insertion\n        // Khuyen khich su dung cau truc du lieu Data Structure\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-077",
                "Skip List Probabilistic",
                "Data Structure",
                "MEDIUM",
                46.7,
                "Cài đặt danh sách nhảy Skip List hỗ trợ tìm kiếm, chèn, xóa phần tử với độ phức tạp trung bình O(log N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Skip List Probabilistic.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Data Structure để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Skip List Probabilistic.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Data Structure\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Skip List Probabilistic.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Data Structure\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Skip List Probabilistic\n        // Khuyen khich su dung cau truc du lieu Data Structure\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-078",
                "Bloom Filter Probabilistic",
                "Data Structure",
                "MEDIUM",
                52.4,
                "Cài đặt bộ lọc Bloom Filter sử dụng k hàm băm để kiểm tra sự tồn tại của phần tử với xác suất dương tính giả thấp.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Bloom Filter Probabilistic.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Data Structure để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Bloom Filter Probabilistic.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Data Structure\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Bloom Filter Probabilistic.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Data Structure\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Bloom Filter Probabilistic\n        // Khuyen khich su dung cau truc du lieu Data Structure\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-079",
                "Count Min Sketch",
                "Streaming",
                "MEDIUM",
                49.0,
                "Cài đặt Count-Min Sketch để ước tính tần suất xuất hiện của các phần tử trong luồng dữ liệu thời gian thực.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Count Min Sketch.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Streaming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Count Min Sketch.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Streaming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Count Min Sketch.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Streaming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Count Min Sketch\n        // Khuyen khich su dung cau truc du lieu Streaming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-080",
                "HyperLogLog Cardinality",
                "Streaming",
                "HARD",
                33.0,
                "Cài đặt thuật toán HyperLogLog để ước lượng số lượng phần tử phân biệt (Cardinality) trong luồng dữ liệu lớn với sai số thấp.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán HyperLogLog Cardinality.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Streaming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán HyperLogLog Cardinality.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Streaming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan HyperLogLog Cardinality.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Streaming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho HyperLogLog Cardinality\n        // Khuyen khich su dung cau truc du lieu Streaming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-081",
                "LRU Cache Implementation",
                "Design",
                "MEDIUM",
                41.5,
                "Thiết kế bộ nhớ đệm Least Recently Used (LRU) hỗ trợ get và put với độ phức tạp O(1) bằng HashMap và Doubly Linked List.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán LRU Cache Implementation.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Design để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán LRU Cache Implementation.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Design\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan LRU Cache Implementation.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Design\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho LRU Cache Implementation\n        // Khuyen khich su dung cau truc du lieu Design\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-082",
                "LFU Cache Implementation",
                "Design",
                "HARD",
                34.2,
                "Thiết kế bộ nhớ đệm Least Frequently Used (LFU) hỗ trợ get và put với độ phức tạp O(1) theo tần suất truy cập.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán LFU Cache Implementation.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Design để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán LFU Cache Implementation.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Design\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan LFU Cache Implementation.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Design\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho LFU Cache Implementation\n        // Khuyen khich su dung cau truc du lieu Design\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-083",
                "Consistent Hashing Ring",
                "Distributed Systems",
                "MEDIUM",
                50.8,
                "Cài đặt vòng băm nhất quán (Consistent Hash Ring) với các nút ảo (Virtual Nodes) phân phối đều tải lưu trữ.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Consistent Hashing Ring.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Distributed Systems để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Consistent Hashing Ring.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Distributed Systems\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Consistent Hashing Ring.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Distributed Systems\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Consistent Hashing Ring\n        // Khuyen khich su dung cau truc du lieu Distributed Systems\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-084",
                "Leaky Bucket Rate Limiter",
                "System Design",
                "MEDIUM",
                56.4,
                "Cài đặt thuật toán Leaky Bucket để định hình lưu lượng mạng (Traffic Shaping) và giới hạn tốc độ yêu cầu.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Leaky Bucket Rate Limiter.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu System Design để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Leaky Bucket Rate Limiter.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng System Design\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Leaky Bucket Rate Limiter.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat System Design\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Leaky Bucket Rate Limiter\n        // Khuyen khich su dung cau truc du lieu System Design\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-085",
                "Token Bucket Rate Limiter",
                "System Design",
                "MEDIUM",
                58.1,
                "Cài đặt thuật toán Token Bucket hỗ trợ giới hạn tốc độ và cho phép burst traffic có kiểm soát.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Token Bucket Rate Limiter.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu System Design để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Token Bucket Rate Limiter.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng System Design\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Token Bucket Rate Limiter.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat System Design\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Token Bucket Rate Limiter\n        // Khuyen khich su dung cau truc du lieu System Design\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-086",
                "Raft Leader Election Simulator",
                "Distributed Systems",
                "HARD",
                30.5,
                "Mô phỏng thuật toán bầu chọn lãnh đạo (Leader Election) và nhịp tim (Heartbeat) trong giao thức đồng thuận Raft.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Raft Leader Election Simulator.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Distributed Systems để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Raft Leader Election Simulator.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Distributed Systems\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Raft Leader Election Simulator.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Distributed Systems\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Raft Leader Election Simulator\n        // Khuyen khich su dung cau truc du lieu Distributed Systems\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-087",
                "MapReduce Word Count",
                "Distributed Systems",
                "MEDIUM",
                65.0,
                "Cài đặt mô hình tính toán MapReduce cơ bản để đếm tần suất xuất hiện của từ trong tập tài liệu văn bản phân tán.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán MapReduce Word Count.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Distributed Systems để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán MapReduce Word Count.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Distributed Systems\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan MapReduce Word Count.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Distributed Systems\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho MapReduce Word Count\n        // Khuyen khich su dung cau truc du lieu Distributed Systems\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-088",
                "PageRank Iterative Algorithm",
                "Algorithms",
                "MEDIUM",
                53.7,
                "Tính toán thứ hạng của các trang web trên đồ thị liên kết bằng phương pháp lặp xích Markov có hệ số suy giảm Damping Factor.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán PageRank Iterative Algorithm.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Algorithms để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán PageRank Iterative Algorithm.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Algorithms\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan PageRank Iterative Algorithm.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Algorithms\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho PageRank Iterative Algorithm\n        // Khuyen khich su dung cau truc du lieu Algorithms\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-089",
                "Vector Cosine Similarity",
                "Information Retrieval",
                "EASY",
                72.0,
                "Tính độ tương đồng Cosine giữa hai vector nhúng đặc trưng (Vector Embeddings) trong không gian n chiều.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Vector Cosine Similarity.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Information Retrieval để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Vector Cosine Similarity.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Information Retrieval\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Vector Cosine Similarity.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Information Retrieval\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Vector Cosine Similarity\n        // Khuyen khich su dung cau truc du lieu Information Retrieval\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-090",
                "B Tree Node Splitting",
                "Database Internals",
                "HARD",
                32.5,
                "Cài đặt cấu trúc nút B-Tree bậc M và giải thuật tách nút khi chèn khóa mới vượt quá dung lượng cho phép.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán B Tree Node Splitting.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Database Internals để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán B Tree Node Splitting.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Database Internals\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan B Tree Node Splitting.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Database Internals\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho B Tree Node Splitting\n        // Khuyen khich su dung cau truc du lieu Database Internals\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-091",
                "Write Ahead Logging Parser",
                "Database Internals",
                "HARD",
                36.0,
                "Thiết kế bộ phân tích nhật ký ghi trước (WAL Parser) để phục hồi trạng thái cơ sở dữ liệu sau sự cố sập nguồn.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Write Ahead Logging Parser.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Database Internals để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Write Ahead Logging Parser.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Database Internals\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Write Ahead Logging Parser.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Database Internals\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Write Ahead Logging Parser\n        // Khuyen khich su dung cau truc du lieu Database Internals\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-092",
                "Log Structured Merge Tree MemTable",
                "Database Internals",
                "HARD",
                35.1,
                "Cài đặt cấu trúc MemTable và chuyển đổi sang SSTable bất biến trong kiến trúc LSM-Tree.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Log Structured Merge Tree MemTable.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Database Internals để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Log Structured Merge Tree MemTable.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Database Internals\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Log Structured Merge Tree MemTable.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Database Internals\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Log Structured Merge Tree MemTable\n        // Khuyen khich su dung cau truc du lieu Database Internals\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-093",
                "Two Phase Commit Coordinator",
                "Distributed Systems",
                "HARD",
                33.4,
                "Mô phỏng bộ điều phối giao dịch phân tán 2PC với pha Prepare và pha Commit / Rollback.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Two Phase Commit Coordinator.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Distributed Systems để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Two Phase Commit Coordinator.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Distributed Systems\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Two Phase Commit Coordinator.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Distributed Systems\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Two Phase Commit Coordinator\n        // Khuyen khich su dung cau truc du lieu Distributed Systems\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-094",
                "Merkle Tree Verification",
                "Cryptography",
                "MEDIUM",
                57.3,
                "Cài đặt cây Merkle Tree để xác thực tính toàn vẹn của các khối dữ liệu bằng hàm băm mật mã SHA-256.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Merkle Tree Verification.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Cryptography để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Merkle Tree Verification.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Cryptography\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Merkle Tree Verification.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Cryptography\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Merkle Tree Verification\n        // Khuyen khich su dung cau truc du lieu Cryptography\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-095",
                "Snowflake ID Generator",
                "Distributed Systems",
                "MEDIUM",
                64.2,
                "Cài đặt giải thuật sinh mã định danh duy nhất toàn cục 64-bit dựa trên thời gian, máy trạm và số thứ tự tự tăng.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Snowflake ID Generator.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Distributed Systems để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Snowflake ID Generator.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Distributed Systems\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Snowflake ID Generator.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Distributed Systems\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Snowflake ID Generator\n        // Khuyen khich su dung cau truc du lieu Distributed Systems\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-096",
                "Thread Pool Work Queue",
                "Concurrency",
                "MEDIUM",
                51.0,
                "Cài đặt Thread Pool tùy biến với Blocking Queue hỗ trợ quản lý worker thread và hàng đợi nhiệm vụ.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Thread Pool Work Queue.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Concurrency để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Thread Pool Work Queue.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Concurrency\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Thread Pool Work Queue.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Concurrency\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Thread Pool Work Queue\n        // Khuyen khich su dung cau truc du lieu Concurrency\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-097",
                "Lock Free Concurrent Queue",
                "Concurrency",
                "HARD",
                29.5,
                "Cài đặt hàng đợi không khóa (Lock-free Queue Michael-Scott) sử dụng AtomicReference và Compare-And-Swap (CAS).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Lock Free Concurrent Queue.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Concurrency để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Lock Free Concurrent Queue.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Concurrency\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Lock Free Concurrent Queue.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Concurrency\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Lock Free Concurrent Queue\n        // Khuyen khich su dung cau truc du lieu Concurrency\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-098",
                "Actor Model Mailbox",
                "Concurrency",
                "MEDIUM",
                47.8,
                "Cài đặt mô hình Actor Model với hòm thư bất đồng bộ (Mailbox) xử lý thông điệp tuần tự tránh tranh chấp bộ nhớ.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Actor Model Mailbox.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Concurrency để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Actor Model Mailbox.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Concurrency\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Actor Model Mailbox.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Concurrency\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Actor Model Mailbox\n        // Khuyen khich su dung cau truc du lieu Concurrency\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-099",
                "Circuit Breaker State Machine",
                "System Design",
                "MEDIUM",
                59.6,
                "Cài đặt máy trạng thái Circuit Breaker với 3 trạng thái CLOSED, OPEN và HALF-OPEN ngắt dòng bảo vệ hệ thống.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Circuit Breaker State Machine.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu System Design để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Circuit Breaker State Machine.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng System Design\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Circuit Breaker State Machine.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat System Design\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Circuit Breaker State Machine\n        // Khuyen khich su dung cau truc du lieu System Design\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-100",
                "Fast Fourier Transform Multiplication",
                "Mathematics",
                "HARD",
                31.0,
                "Nhân hai đa thức bậc N trong O(N log N) bằng giải thuật biến đổi Fourier nhanh (Cooley-Tukey FFT).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Fast Fourier Transform Multiplication.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Mathematics để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Fast Fourier Transform Multiplication.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Mathematics\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Fast Fourier Transform Multiplication.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Mathematics\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Fast Fourier Transform Multiplication\n        // Khuyen khich su dung cau truc du lieu Mathematics\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-101",
                "Matrix Exponentiation Fibonacci",
                "Mathematics",
                "MEDIUM",
                62.5,
                "Tính số Fibonacci thứ N với N lên đến 10^18 trong thời gian O(log N) bằng lũy thừa ma trận nhị phân.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Matrix Exponentiation Fibonacci.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Mathematics để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Matrix Exponentiation Fibonacci.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Mathematics\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Matrix Exponentiation Fibonacci.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Mathematics\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Matrix Exponentiation Fibonacci\n        // Khuyen khich su dung cau truc du lieu Mathematics\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-102",
                "Sudoku Solver Backtracking",
                "Backtracking",
                "HARD",
                43.8,
                "Giải bảng Sudoku 9x9 bằng thuật toán quay lui kết hợp cắt tỉa nhánh kiểm tra hợp lệ.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Sudoku Solver Backtracking.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Backtracking để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Sudoku Solver Backtracking.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Backtracking\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Sudoku Solver Backtracking.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Backtracking\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Sudoku Solver Backtracking\n        // Khuyen khich su dung cau truc du lieu Backtracking\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-103",
                "N Queens Problem",
                "Backtracking",
                "HARD",
                39.2,
                "Tìm tất cả các cách xếp N quân hậu trên bàn cờ N x N sao cho không có hai quân hậu nào khống chế nhau.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán N Queens Problem.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Backtracking để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán N Queens Problem.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Backtracking\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan N Queens Problem.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Backtracking\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho N Queens Problem\n        // Khuyen khich su dung cau truc du lieu Backtracking\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-104",
                "Sliding Window Maximum Deque",
                "Data Structure",
                "HARD",
                46.5,
                "Tìm phần tử lớn nhất trong mỗi cửa sổ trượt kích thước k di chuyển trên mảng bằng hàng đợi 2 đầu đơn điệu O(N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Sliding Window Maximum Deque.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Data Structure để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Sliding Window Maximum Deque.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Data Structure\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Sliding Window Maximum Deque.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Data Structure\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Sliding Window Maximum Deque\n        // Khuyen khich su dung cau truc du lieu Data Structure\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-105",
                "Largest Rectangle in Histogram",
                "Stack",
                "HARD",
                43.1,
                "Tìm diện tích hình chữ nhật lớn nhất trong biểu đồ cột bằng ngăn xếp đơn điệu (Monotonic Stack) trong O(N).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Largest Rectangle in Histogram.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Stack để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Largest Rectangle in Histogram.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Stack\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Largest Rectangle in Histogram.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Stack\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Largest Rectangle in Histogram\n        // Khuyen khich su dung cau truc du lieu Stack\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-106",
                "Maximal Rectangle Binary Matrix",
                "Dynamic Programming",
                "HARD",
                45.3,
                "Tìm diện tích hình chữ nhật lớn nhất chỉ chứa toàn số 1 trong ma trận nhị phân 2D.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Maximal Rectangle Binary Matrix.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Maximal Rectangle Binary Matrix.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Maximal Rectangle Binary Matrix.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Maximal Rectangle Binary Matrix\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-107",
                "Median of Two Sorted Arrays",
                "Binary Search",
                "HARD",
                38.6,
                "Tìm giá trị trung vị của hai mảng số nguyên đã sắp xếp với độ phức tạp thời gian O(log(min(M, N))).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Median of Two Sorted Arrays.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Binary Search để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Median of Two Sorted Arrays.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Binary Search\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Median of Two Sorted Arrays.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Binary Search\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Median of Two Sorted Arrays\n        // Khuyen khich su dung cau truc du lieu Binary Search\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-108",
                "Regular Expression Matching",
                "Dynamic Programming",
                "HARD",
                28.5,
                "Kiểm tra chuỗi s có khớp với biểu thức chính quy p hỗ trợ ký tự đại diện '.' và lặp '*' hay không.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Regular Expression Matching.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Regular Expression Matching.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Regular Expression Matching.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Regular Expression Matching\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-109",
                "Wildcard Matching",
                "Dynamic Programming",
                "HARD",
                27.2,
                "Kiểm tra chuỗi s có khớp với mẫu p hỗ trợ '?' (khớp 1 ký tự) và '*' (khớp chuỗi bất kỳ).\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Wildcard Matching.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Wildcard Matching.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Wildcard Matching.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Wildcard Matching\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-110",
                "Valid Number Finite State Machine",
                "Automata",
                "HARD",
                19.4,
                "Kiểm tra xem chuỗi đầu vào có phải là biểu diễn số hợp lệ hay không bằng máy trạng thái hữu hạn DFA.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Valid Number Finite State Machine.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Automata để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Valid Number Finite State Machine.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Automata\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Valid Number Finite State Machine.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Automata\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Valid Number Finite State Machine\n        // Khuyen khich su dung cau truc du lieu Automata\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-111",
                "Text Justification Greedy",
                "Simulation",
                "HARD",
                31.5,
                "Căn đều hai bên văn bản (Justify) thành các dòng có độ rộng cố định maxWidth sao cho các khoảng trắng phân bổ đều.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Text Justification Greedy.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Simulation để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Text Justification Greedy.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Simulation\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Text Justification Greedy.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Simulation\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Text Justification Greedy\n        // Khuyen khich su dung cau truc du lieu Simulation\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-112",
                "Basic Calculator III Parentheses",
                "Stack",
                "HARD",
                48.7,
                "Tính giá trị biểu thức số học chứa các toán tử +, -, *, / và dấu ngoặc đơn lồng nhau.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Basic Calculator III Parentheses.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Stack để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Basic Calculator III Parentheses.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Stack\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Basic Calculator III Parentheses.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Stack\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Basic Calculator III Parentheses\n        // Khuyen khich su dung cau truc du lieu Stack\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-113",
                "Trapping Rain Water 3D",
                "Heap",
                "HARD",
                46.0,
                "Tính thể tích nước mưa giữ lại được trên bản đồ địa hình độ cao 3D bằng hàng đợi ưu tiên Min-Heap.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Trapping Rain Water 3D.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Heap để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Trapping Rain Water 3D.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Heap\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Trapping Rain Water 3D.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Heap\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Trapping Rain Water 3D\n        // Khuyen khich su dung cau truc du lieu Heap\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-114",
                "Alien Dictionary Topological Sort",
                "Graph",
                "HARD",
                35.8,
                "Xác định thứ tự chữ cái trong ngôn ngữ người ngoài hành tinh từ danh sách các từ đã được sắp xếp từ điển.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Alien Dictionary Topological Sort.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Alien Dictionary Topological Sort.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Alien Dictionary Topological Sort.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Alien Dictionary Topological Sort\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-115",
                "Reconstruct Itinerary Eulerian Path",
                "Graph",
                "HARD",
                42.0,
                "Tìm hành trình bay hợp lệ bắt đầu từ JFK đi qua tất cả các vé máy bay đúng 1 lần bằng giải thuật Hierholzer.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Reconstruct Itinerary Eulerian Path.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Graph để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Reconstruct Itinerary Eulerian Path.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Graph\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Reconstruct Itinerary Eulerian Path.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Graph\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Reconstruct Itinerary Eulerian Path\n        // Khuyen khich su dung cau truc du lieu Graph\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-116",
                "Binary Tree Maximum Path Sum NonRoot",
                "Tree",
                "HARD",
                39.0,
                "Tìm đường đi có tổng lớn nhất giữa hai nút bất kỳ trong cây có trọng số âm.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Binary Tree Maximum Path Sum NonRoot.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Tree để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Binary Tree Maximum Path Sum NonRoot.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Tree\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Binary Tree Maximum Path Sum NonRoot.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Tree\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Binary Tree Maximum Path Sum NonRoot\n        // Khuyen khich su dung cau truc du lieu Tree\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-117",
                "Palindrome Pairs",
                "Trie",
                "HARD",
                35.2,
                "Tìm tất cả các cặp chỉ số (i, j) sao cho nối hai chuỗi words[i] + words[j] tạo thành chuỗi đối xứng.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Palindrome Pairs.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Trie để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Palindrome Pairs.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Trie\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Palindrome Pairs.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Trie\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Palindrome Pairs\n        // Khuyen khich su dung cau truc du lieu Trie\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-118",
                "Shortest Palindrome KMP",
                "String",
                "HARD",
                33.6,
                "Tìm chuỗi đối xứng ngắn nhất có thể tạo ra bằng cách thêm các ký tự vào đầu chuỗi ban đầu.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Shortest Palindrome KMP.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu String để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Shortest Palindrome KMP.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng String\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Shortest Palindrome KMP.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat String\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Shortest Palindrome KMP\n        // Khuyen khich su dung cau truc du lieu String\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-119",
                "Distinct Subsequences Count",
                "Dynamic Programming",
                "HARD",
                44.8,
                "Đếm số dãy con khác nhau của chuỗi s xuất hiện đúng bằng chuỗi t bằng quy hoạch động.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Distinct Subsequences Count.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Dynamic Programming để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Distinct Subsequences Count.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Dynamic Programming\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Distinct Subsequences Count.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Dynamic Programming\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Distinct Subsequences Count\n        // Khuyen khich su dung cau truc du lieu Dynamic Programming\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

        registerProblem(new ProblemItem(
                "PROB-120",
                "Longest Valid Parentheses",
                "Stack",
                "HARD",
                33.2,
                "Tìm độ dài chuỗi con ngoặc hợp lệ dài nhất trong chuỗi ngoặc cho trước.\\n\\nĐịnh dạng đầu vào:\\n- Dòng 1: Kích thước mảng hoặc số lượng phần tử N.\\n- Dòng 2..k: Các phần tử của dữ liệu đầu vào.\\n\\nĐịnh dạng đầu ra:\\n- In ra kết quả tương ứng theo yêu cầu bài toán.\\n\\nRàng buộc:\\n- 1 <= N <= 100,000\\n- Các giá trị nằm trong khoảng [-10^9, 10^9]\\n- Thời gian chạy tối đa: 1.0 giây\\n- Giới hạn bộ nhớ: 256 MB.",
                List.of(
                        "Phân tích độ phức tạp thời gian và không gian tối ưu cho bài toán Longest Valid Parentheses.",
                        "Xem xét các trường hợp biên đặc biệt như mảng rỗng, mảng 1 phần tử hoặc giá trị tràn số.",
                        "Áp dụng cấu trúc dữ liệu Stack để tối ưu hóa thời gian xử lý xuống cận dưới lý thuyết.",
                        "Kiểm tra tính đúng đắn với các bộ dữ liệu lớn n = 10^5 để đảm bảo không bị quá thời gian (TLE)."
                ),
                "import java.util.*;\n\npublic class Solution {\n    /**\n     * Giải thuật tối ưu cho bài toán Longest Valid Parentheses.\n     * Độ phức tạp kỳ vọng: O(N log N) hoặc O(N).\n     */\n    public Object solve(int n, int[] nums, Object extraParam) {\n        // Khởi tạo các cấu trúc dữ liệu hỗ trợ\n        // Thực hiện xử lý chính theo tư tưởng Stack\n        // Trả về kết quả tối ưu\n        return null;\n    }\n}",
                "from typing import List, Optional, Any\n\nclass Solution:\n    def solve(self, n: int, nums: List[int], extra_param: Any = None) -> Any:\n        \"\"\"\n        Giai thuat toi uu cho bai toan Longest Valid Parentheses.\n        Do phuc tap ky vong: O(N log N) hoac O(N).\n        \"\"\"\n        # TODO: Cai dat giai thuat Stack\n        pass",
                "#include <iostream>\n#include <vector>\n#include <string>\n#include <algorithm>\n#include <unordered_map>\n\nusing namespace std;\n\nclass Solution {\npublic:\n    void solve(int n, const vector<int>& nums) {\n        // TODO: Cai dat giai thuat toi uu cho Longest Valid Parentheses\n        // Khuyen khich su dung cau truc du lieu Stack\n    }\n};",
                List.of(
                        new TestCase("4\n2 7 11 15\n9", "0 1", false),
                        new TestCase("3\n3 2 4\n6", "1 2", false),
                        new TestCase("2\n3 3\n6", "0 1", false),
                        new TestCase("1000\n[large test dataset]\ntarget", "[hidden output]", true),
                        new TestCase("100000\n[extreme stress test dataset]\ntarget", "[hidden stress output]", true)
                )
        ));

    }

    private static void registerProblem(ProblemItem prob) {
        PROBLEMS.put(prob.id(), prob);
    }

    public static List<ProblemItem> getAllProblems() {
        return List.copyOf(PROBLEMS.values());
    }

    public static Optional<ProblemItem> findById(String id) {
        return Optional.ofNullable(PROBLEMS.get(id));
    }

    public static Optional<ProblemItem> findByCode(String code) {
        return findById(code);
    }

    public static List<ProblemItem> findByCategory(String category) {
        return PROBLEMS.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .toList();
    }

    public static List<ProblemItem> findByDifficulty(String difficulty) {
        return PROBLEMS.values().stream()
                .filter(p -> p.difficulty().equalsIgnoreCase(difficulty))
                .toList();
    }

    public static List<ProblemItem> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllProblems();
        }
        String q = query.toLowerCase();
        return PROBLEMS.values().stream()
                .filter(p -> p.title().toLowerCase().contains(q) || p.description().toLowerCase().contains(q))
                .toList();
    }

    public static int getTotalProblemCount() {
        return PROBLEMS.size();
    }
}
