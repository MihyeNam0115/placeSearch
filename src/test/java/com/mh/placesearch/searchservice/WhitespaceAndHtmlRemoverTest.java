package com.mh.placesearch.searchservice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WhitespaceAndHtmlRemoverTest {

    @Test
    void apply_whenWithSpace_shouldRemoveSpace() {
        List<String> source = Arrays.asList("abc abc", " abcabc", "abcabc ", " abc abc ", "abc  a b c");
        List<String> actual = source.stream().map(WhitespaceAndHtmlRemover::apply).collect(Collectors.toList());

        assertThat(actual).hasSize(5).isEqualTo(Arrays.asList("abcabc", "abcabc", "abcabc", "abcabc", "abcabc"));
    }

    @Test
    void apply_whenWithTab_shouldRemoveTab() {
        List<String> source = Arrays.asList("abc\tabc", "\tabcabc", "abcabc\t", "\tabc\tabc ", "abc\ta\tb\tc");
        List<String> actual = source.stream().map(WhitespaceAndHtmlRemover::apply).collect(Collectors.toList());

        assertThat(actual).hasSize(5).isEqualTo(Arrays.asList("abcabc", "abcabc", "abcabc", "abcabc", "abcabc"));
    }

    @Test
    void apply_whenWithNewLine_shouldRemoveTab() {
        List<String> source = Arrays.asList("abc\nabc", "\nabcabc", "abcabc\n", "\nabc\nabc ", "abc\na\nb\nc");
        List<String> actual = source.stream().map(WhitespaceAndHtmlRemover::apply).collect(Collectors.toList());

        assertThat(actual).hasSize(5).isEqualTo(Arrays.asList("abcabc", "abcabc", "abcabc", "abcabc", "abcabc"));
    }


    @Test
    void apply_whenWithHtmlTag_shouldRemoveTab() {
        List<String> source = Arrays.asList("abc<b>abc", "</b>abcabc", "abcabc</b>", "<b>abc</b>abc ", "abc<b>a</b>b<b>c");
        List<String> actual = source.stream().map(WhitespaceAndHtmlRemover::apply).collect(Collectors.toList());

        assertThat(actual).hasSize(5).isEqualTo(Arrays.asList("abcabc", "abcabc", "abcabc", "abcabc", "abcabc"));
    }
}