package telekocsi.utils;

import org.springframework.ui.Model;
import java.util.List;

public class PaginationHelper {

    public static <T> void applyPaginationToModel(Model model, List<T> allItems, int page, int size, String contentAttributeName) {
        final int safeSize = (size <= 0) ? 1 : size;
        final int totalItems = allItems.size();
        final int totalPages = (totalItems == 0) ? 1 : (totalItems + safeSize - 1) / safeSize;
        final int safePage = Math.min(Math.max(0, page), totalPages - 1);
        final int fromIndex = safePage * safeSize;
        final int toIndex = Math.min(fromIndex + safeSize, totalItems);
        final List<T> contentPage = (fromIndex >= totalItems) ? List.of() : allItems.subList(fromIndex, toIndex);

        model.addAttribute(contentAttributeName, contentPage);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", safeSize);
    }
}