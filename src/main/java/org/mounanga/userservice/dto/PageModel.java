package org.mounanga.userservice.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PageModel<T> {
    private boolean hasContent;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;
    private int page;
    private int totalPages;
    private int size;
    private long totalElements;
    private int numberOfElements;
    private int numbers;
    private List<T> content;

}
