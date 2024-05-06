package io.openems.edge.levl.controller.controllers.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilTest {

    @Test
    void join() {
        var list1 = List.of(1, 2, 3);
        var list2 = List.of(4, 5, 6);
        var list3 = List.of(7, 8, 9);
        var result = CollectionUtil.join(list1, list2, list3);
        assertThat(result).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

}