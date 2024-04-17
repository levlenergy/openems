package io.openems.edge.levl.controller.workflow;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DischargeRequestTest {

    public static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    public void of() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z","Req01", 1000, 20, 10);
        var expected = new DischargeRequest("Req01", "2024-02-15 15:00:00Z", 900000, NOW.plusSeconds(20) , NOW.plusSeconds(30), true);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void inactiveRequest() {
        assertThat(DischargeRequest.inactiveRequest().isActive()).isFalse();
    }

    @Test
    public void isSameRequest_DifferentId() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 0, 10);
        assertThat(result.isSameRequest(DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req02", 1000, 0, 10))).isFalse();
    }

    @Test
    public void isSameRequest_SameId() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 0, 10);
        assertThat(result.isSameRequest(DischargeRequest.of(NOW.plusSeconds(6), "2024-02-15 15:00:00Z", "Req01", 2000, 0, 20))).isTrue();
    }

    @Test
    public void isExpired_JustNotYet() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 0, 10);
        assertThat(result.isExpired(NOW.plusSeconds(10))).isFalse();
    }

    @Test
    public void isExpired_JustExpired() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 0, 10);
        assertThat(result.isExpired(NOW.plusSeconds(11))).isTrue();
    }

    @Test
    public void shouldStart_JustNotYet() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 10, 0);
        assertThat(result.shouldStart(NOW.plusSeconds(10))).isFalse();
    }

    @Test
    public void shouldStart_JustNow() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 10, 0);
        assertThat(result.shouldStart(NOW.plusSeconds(11))).isTrue();
    }

    @Test
    public void save() {
        var result = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 10, 0);
        var expected = new DischargeRequest.DischargeRequestMemento("Req01", "2024-02-15 15:00:00Z", 900000, NOW.plusSeconds(10), NOW.plusSeconds(10), true);

       var actual = result.save();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void restore() {
        var memento = new DischargeRequest.DischargeRequestMemento("Req01", "2024-02-15 15:00:00Z", 900000, NOW.plusSeconds(10), NOW.plusSeconds(10), true);
        var expected = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "Req01", 1000, 10, 0);

        var result = DischargeRequest.restore(memento);

        assertThat(result).isEqualTo(expected);
    }
}