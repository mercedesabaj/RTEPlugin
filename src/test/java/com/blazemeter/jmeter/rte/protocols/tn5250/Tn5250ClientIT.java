package com.blazemeter.jmeter.rte.protocols.tn5250;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.blazemeter.jmeter.rte.core.AttentionKey;
import com.blazemeter.jmeter.rte.core.CoordInput;
import com.blazemeter.jmeter.rte.core.Input;
import com.blazemeter.jmeter.rte.core.LabelInput;
import com.blazemeter.jmeter.rte.core.NavigationInput;
import com.blazemeter.jmeter.rte.core.Position;
import com.blazemeter.jmeter.rte.core.Screen;
import com.blazemeter.jmeter.rte.core.Screen.Segment;
import com.blazemeter.jmeter.rte.core.Screen.Segment.SegmentBuilder;
import com.blazemeter.jmeter.rte.core.TerminalType;
import com.blazemeter.jmeter.rte.core.exceptions.InvalidFieldLabelException;
import com.blazemeter.jmeter.rte.core.exceptions.InvalidFieldPositionException;
import com.blazemeter.jmeter.rte.core.exceptions.RteIOException;
import com.blazemeter.jmeter.rte.core.listener.TerminalStateListener;
import com.blazemeter.jmeter.rte.core.ssl.SSLContextFactory;
import com.blazemeter.jmeter.rte.core.ssl.SSLType;
import com.blazemeter.jmeter.rte.core.wait.Area;
import com.blazemeter.jmeter.rte.core.wait.CursorWaitCondition;
import com.blazemeter.jmeter.rte.core.wait.DisconnectWaitCondition;
import com.blazemeter.jmeter.rte.core.wait.SilentWaitCondition;
import com.blazemeter.jmeter.rte.core.wait.SyncWaitCondition;
import com.blazemeter.jmeter.rte.core.wait.TextWaitCondition;
import com.blazemeter.jmeter.rte.core.wait.WaitCondition;
import com.blazemeter.jmeter.rte.protocols.RteProtocolClientIT;
import com.blazemeter.jmeter.rte.sampler.NavigationType;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.junit.Test;

public class Tn5250ClientIT extends RteProtocolClientIT<Tn5250Client> {

  private static final String TEST_USERNAME = "TESTUSR";
  private static final String TEST_PASSWORD = "TESTPSW";

  @Override
  protected Tn5250Client buildClient() {
    return new Tn5250Client();
  }

  @Override
  protected TerminalType getDefaultTerminalType() {
    return client.getTerminalTypeById("IBM-3477-FC");
  }

  @Override
  protected List<Segment> buildExpectedFields() {
    return Arrays.asList(
        new SegmentBuilder().withPosition(6, 53).withText(buildBlankSpaces())
            .withEditable().build(SCREEN_SIZE),
        new SegmentBuilder().withPosition(7, 53).withText(buildBlankSpaces())
            .withEditable().withSecret().build(SCREEN_SIZE),
        new SegmentBuilder().withPosition(8, 53).withText(buildBlankSpaces())
            .withEditable().build(SCREEN_SIZE),
        new SegmentBuilder().withPosition(9, 53).withText(buildBlankSpaces())
            .withEditable().build(SCREEN_SIZE),
        new SegmentBuilder().withPosition(10, 53).withText(buildBlankSpaces())
            .withEditable().build(SCREEN_SIZE)
    );
  }

  private String buildBlankSpaces() {
    return StringUtils.repeat("\u0000", 10);
  }


  @Test
  public void shouldGetWelcomeScreenWhenConnect() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    assertThat(client.getScreen().withInvisibleCharsToSpaces())
        .isEqualTo(buildLoginWelcomeScreen());
  }

  private void loadLoginFlow() throws FileNotFoundException {
    loadFlow("login-immediate-responses.yml");
  }

  private Screen buildLoginWelcomeScreen() throws java.io.IOException {
    return buildScreenFromHtmlFile("login-welcome-screen.html");
  }

  @Test
  public void shouldGetWelcomeScreenWhenConnectWithSsl() throws Exception {
    server.stop(SERVER_STOP_TIMEOUT);
    loadLoginFlow();
    SSLContextFactory.setKeyStore(findResource("/.keystore").getFile());
    SSLContextFactory.setKeyStorePassword("changeit");
    server.setSslEnabled(true);
    server.start();
    client.connect(VIRTUAL_SERVER_HOST, server.getPort(), SSLType.TLS, getDefaultTerminalType(),
        TIMEOUT_MILLIS);
    waitSync();
    assertThat(client.getScreen().withInvisibleCharsToSpaces())
        .isEqualTo(buildLoginWelcomeScreen());
  }

  @Test(expected = RteIOException.class)
  public void shouldThrowRteIOExceptionWhenConnectWithInvalidPort() throws Exception {
    client.connect(VIRTUAL_SERVER_HOST, 1, SSLType.NONE, getDefaultTerminalType(), TIMEOUT_MILLIS);
  }

  @Test
  public void shouldGetUserMenuScreenWhenSendCredsByCoord() throws Exception {
    loadFlow("login.yml");
    connectToVirtualService();
    sendCredsByCoordWithSyncWait();
    assertThat(client.getScreen().withInvisibleCharsToSpaces()).isEqualTo(buildUserMenuScreen());
  }

  private Screen buildUserMenuScreen() throws java.io.IOException {
    return buildScreenFromHtmlFile("user-menu-screen.html");
  }

  private void sendCredsByCoordWithSyncWait() throws Exception {
    client.send(buildCredsFieldsByCoord(), AttentionKey.ENTER, 0);
    waitSync();
  }

  private void waitSync() throws InterruptedException, TimeoutException, RteIOException {
    client.await(
        Collections.singletonList(new SyncWaitCondition(TIMEOUT_MILLIS, STABLE_TIMEOUT_MILLIS)));
  }

  private List<Input> buildCredsFieldsByCoord() {
    return Arrays.asList(
        new CoordInput(new Position(6, 53), TEST_USERNAME),
        new CoordInput(new Position(7, 53), TEST_PASSWORD));
  }

  @Test
  public void shouldGetUserMenuScreenWhenSendCredsByLabel() throws Exception {
    loadFlow("login.yml");
    connectToVirtualService();
    sendCredsByLabelWithSyncWait();
    assertThat(client.getScreen().withInvisibleCharsToSpaces()).isEqualTo(buildUserMenuScreen());
  }

  private void sendCredsByLabelWithSyncWait() throws Exception {
    client.send(buildCredsFieldsByLabel(), AttentionKey.ENTER, 0);
    waitSync();
  }

  private List<Input> buildCredsFieldsByLabel() {
    return Arrays.asList(
        new LabelInput("User", TEST_USERNAME),
        new LabelInput("Password", TEST_PASSWORD));
  }

  @Test(expected = InvalidFieldPositionException.class)
  public void shouldThrowInvalidFieldPositionExceptionWhenSendIncorrectFieldPositionByCoord()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    List<Input> input = Collections.singletonList(
        new CoordInput(new Position(7, 1), TEST_USERNAME));
    client.send(input, AttentionKey.ENTER, 0);
  }

  @Test(expected = InvalidFieldLabelException.class)
  public void shouldThrowInvalidFieldPositionExceptionWhenSendIncorrectFieldPositionByLabel()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    List<Input> input = Collections.singletonList(
        new LabelInput("Usr", TEST_USERNAME));
    client.send(input, AttentionKey.ENTER, 0);
  }

  @Test
  public void shouldGetTrueSoundAlarmWhenServerSendTheSignal() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    sendCredsByCoordWithSyncWait();
    assertThat(client.resetAlarm()).isTrue();
  }

  @Test
  public void shouldGetFalseSoundAlarmWhenServerDoNotSendTheSignal() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    assertThat(client.resetAlarm()).isFalse();
  }

  @Test(expected = RteIOException.class)
  public void shouldThrowRteIOExceptionWhenSendAndServerDown() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    server.stop(SERVER_STOP_TIMEOUT);
    sendCredsByCoordWithSyncWait();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUnsupportedOperationExceptionWhenAwaitWithUndefinedCondition()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    List<WaitCondition> conditions = Collections
        .singletonList(new WaitCondition(TIMEOUT_MILLIS, STABLE_TIMEOUT_MILLIS) {
          @Override
          public String getDescription() {
            return "test";
          }
        });
    client.await(conditions);
  }

  @Test(expected = TimeoutException.class)
  public void shouldThrowTimeoutExceptionWhenSyncWaitAndSlowResponse() throws Exception {
    loadFlow("slow-response.yml");
    connectToVirtualService();
    client.send(buildCredsFieldsByCoord(), AttentionKey.ENTER, 0);
    waitSync();
  }

  @Test(expected = TimeoutException.class)
  public void shouldThrowTimeoutExceptionWhenCursorWaitAndNotExpectedCursorPosition()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    client.send(buildCredsFieldsByCoord(), AttentionKey.ENTER, 0);
    client.await(Collections.singletonList(
        new CursorWaitCondition(new Position(1, 1), TIMEOUT_MILLIS, STABLE_TIMEOUT_MILLIS)));
  }

  @Test(expected = TimeoutException.class)
  public void shouldThrowTimeoutExceptionWhenSilentWaitAndChattyServer() throws Exception {
    loadFlow("chatty-server.yml");
    connectToVirtualService();
    client.send(buildCredsFieldsByCoord(), AttentionKey.ENTER, 0);
    client.await(
        Collections.singletonList(new SilentWaitCondition(TIMEOUT_MILLIS, STABLE_TIMEOUT_MILLIS)));
  }

  @Test(expected = TimeoutException.class)
  public void shouldThrowTimeoutExceptionWhenTextWaitWithNoMatchingRegex()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    client.send(buildCredsFieldsByCoord(), AttentionKey.ENTER, 0);
    client.await(Collections
        .singletonList(new TextWaitCondition(new Perl5Compiler().compile("testing-wait-text"),
            new Perl5Matcher(),
            Area.fromTopLeftBottomRight(1, 1, Position.UNSPECIFIED_INDEX,
                Position.UNSPECIFIED_INDEX),
            TIMEOUT_MILLIS,
            STABLE_TIMEOUT_MILLIS)));
  }

  @Test
  public void shouldGetWelcomeScreenWhenConnectAfterDisconnect() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    sendCredsByCoordWithSyncWait();
    client.disconnect();
    connectToVirtualService();
    assertThat(client.getScreen().withInvisibleCharsToSpaces())
        .isEqualTo(buildLoginWelcomeScreen());
  }

  @Test
  public void shouldNotThrowExceptionWhenDisconnectAndServerDown() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    server.stop(SERVER_STOP_TIMEOUT);
    client.disconnect();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUnsupportedOperationExceptionWhenSelectAttentionKeyUnsupported()
      throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    client.send(buildCredsFieldsByCoord(), AttentionKey.PA1, 0);
  }

  @Test
  public void shouldNotifyAddedListenerWhenTerminalStateChanges() throws Exception {

    TerminalStateListener terminalEmulatorUpdater = mock(TerminalStateListener.class);

    loadLoginFlow();
    connectToVirtualService();

    client.addTerminalStateListener(terminalEmulatorUpdater);

    sendCredsByCoordWithSyncWait();

    /*
     * When inputs are sent to client 2 changes happen: screen change and mouse moved
     * */

    verify(terminalEmulatorUpdater, times(2)).onTerminalStateChange();
  }

  @Test
  public void shouldNotNotifyRemovedListenerWhenTerminalStateChanges() throws Exception {

    TerminalStateListener terminalEmulatorUpdater = mock(TerminalStateListener.class);

    loadLoginFlow();
    connectToVirtualService();

    client.addTerminalStateListener(terminalEmulatorUpdater);
    client.removeTerminalStateListener(terminalEmulatorUpdater);

    sendCredsByCoordWithSyncWait();

    verify(terminalEmulatorUpdater, never()).onTerminalStateChange();
  }

  @Test
  public void shouldValidateSecretFieldsOnScreenWhenBuildScreen() throws Exception {
    loadLoginFlow();
    connectToVirtualService();
    List<Segment> currentFields = client.getScreen().getSegments().stream()
        .filter(Segment::isEditable)
        .collect(
            Collectors.toList());
    assertThat(currentFields).isEqualTo(buildExpectedFields());
  }

  @Test(expected = TimeoutException.class)
  public void shouldFailWhenMatchedScreenChangedBeforeStablePeriod() throws Exception {
    loadFlow("login-with-multiple-flash-screen.yml");
    client.connect(VIRTUAL_SERVER_HOST, server.getPort(), SSLType.NONE, getDefaultTerminalType(),
        TIMEOUT_MILLIS);
    client.await(Collections.singletonList(new TextWaitCondition(JMeterUtils.getPattern("Sign on"),
        JMeterUtils.getMatcher(), Area.fromTopLeftBottomRight(1, 1, 24, 80), 10000, 1000)));
  }

  @Test
  public void shouldGetUserMenuScreenWhenSendNavigationArrowInputs() throws Exception {
    loadFlow("login.yml");
    connectToVirtualService();
    List<Input> inputs = buildLoginInputs();
    client.send(inputs, AttentionKey.ENTER, 0);
    waitSync();
    assertThat(client.getScreen().withInvisibleCharsToSpaces()).isEqualTo(buildUserMenuScreen());
  }

  @Test
  public void shouldWaitForDisconnectWhenServerDisconnects() throws Exception {
    loadFlow("login-and-disconnect.yml");
    connectToVirtualService();
    List<Input> inputs = buildLoginInputs();
    isDisconnectionExpected.update(true);
    client.send(inputs, AttentionKey.ENTER, 0);
    client.await(
        Collections.singletonList(new DisconnectWaitCondition(TIMEOUT_MILLIS)));
  }

  private List<Input> buildLoginInputs() {
    return Arrays.asList(
        new NavigationInput(0, NavigationType.DOWN, "TESTUSR"),
        new NavigationInput(160, NavigationType.RIGHT, ""),
        new NavigationInput(1, NavigationType.UP, ""),
        new NavigationInput(7, NavigationType.LEFT, "TESTPSW"));
  }

  @Test(expected = TimeoutException.class)
  public void shouldThrowTimeoutExceptionWhenWaitForDisconnectAndServerNotDisconnect()
      throws Exception {
    loadFlow("login.yml");
    connectToVirtualService();
    List<Input> inputs = buildLoginInputs();
    client.send(inputs, AttentionKey.ENTER, 0);
    client.await(
        Collections.singletonList(new DisconnectWaitCondition(TIMEOUT_MILLIS)));
  }

}
