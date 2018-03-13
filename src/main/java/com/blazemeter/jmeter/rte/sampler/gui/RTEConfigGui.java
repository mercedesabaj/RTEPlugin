package com.blazemeter.jmeter.rte.sampler.gui;

import com.blazemeter.jmeter.rte.core.Protocol;
import com.blazemeter.jmeter.rte.core.SSLType;
import com.blazemeter.jmeter.rte.core.TerminalType;
import com.blazemeter.jmeter.rte.sampler.RTESampler;
import java.awt.BorderLayout;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.testelement.TestElement;

public class RTEConfigGui extends AbstractConfigGui {

  private static final long serialVersionUID = 8495980373764997386L;
  private RTEConfigPanel rteConfigPanelConfigPanel;

  public RTEConfigGui() {
    rteConfigPanelConfigPanel = new RTEConfigPanel();

    setLayout(new BorderLayout(0, 5));
    setBorder(makeBorder());

    add(makeTitlePanel(), BorderLayout.NORTH);
    add(rteConfigPanelConfigPanel, BorderLayout.CENTER);
  }

  @Override
  public String getStaticLabel() {
    return "RTE Config";
  }

  @Override
  public String getLabelResource() {
    throw new IllegalStateException("This shouldn't be called"); //$NON-NLS-1$
  }

  @Override
  public void configure(TestElement element) {
    super.configure(element);
    if (element instanceof ConfigTestElement) {
      ConfigTestElement configTestElement = (ConfigTestElement) element;
      rteConfigPanelConfigPanel
          .setServer(configTestElement.getPropertyAsString(RTESampler.CONFIG_SERVER));
      rteConfigPanelConfigPanel.setPort(
          configTestElement.getPropertyAsString(RTESampler.CONFIG_PORT,
              String.valueOf(RTESampler.DEFAULT_PORT)));
      rteConfigPanelConfigPanel.setProtocol(
          Protocol.valueOf(configTestElement.getPropertyAsString(RTESampler.CONFIG_PROTOCOL,
              RTESampler.DEFAULT_PROTOCOL.name())));
      rteConfigPanelConfigPanel.setTerminalType(TerminalType
          .valueOf(configTestElement.getPropertyAsString(RTESampler.CONFIG_TERMINAL_TYPE,
              RTESampler.DEFAULT_TERMINAL_TYPE.name())));
      rteConfigPanelConfigPanel.setSSLType(
          SSLType.valueOf(configTestElement
              .getPropertyAsString(RTESampler.CONFIG_SSL_TYPE, RTESampler.DEFAULT_SSLTYPE.name())));
      rteConfigPanelConfigPanel
          .setConnectionTimeout(
              configTestElement.getPropertyAsString(RTESampler.CONFIG_CONNECTION_TIMEOUT,
                  String.valueOf(RTESampler.DEFAULT_CONNECTION_TIMEOUT_MILLIS)));
    }
  }

  @Override
  public TestElement createTestElement() {
    ConfigTestElement config = new ConfigTestElement();
    config.setName(this.getName());
    config.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
    config.setProperty(TestElement.TEST_CLASS, config.getClass().getName());
    modifyTestElement(config);
    return config;
  }

  @Override
  public void modifyTestElement(TestElement te) {
    configureTestElement(te);
    if (te instanceof ConfigTestElement) {
      ConfigTestElement configTestElement = (ConfigTestElement) te;
      configTestElement
          .setProperty(RTESampler.CONFIG_SERVER, rteConfigPanelConfigPanel.getServer());
      configTestElement.setProperty(RTESampler.CONFIG_PORT, rteConfigPanelConfigPanel.getPort());
      configTestElement
          .setProperty(RTESampler.CONFIG_PROTOCOL, rteConfigPanelConfigPanel.getProtocol().name());
      configTestElement
          .setProperty(RTESampler.CONFIG_SSL_TYPE, rteConfigPanelConfigPanel.getSSLType().name());
      configTestElement.setProperty(RTESampler.CONFIG_TERMINAL_TYPE,
          rteConfigPanelConfigPanel.getTerminalType().name());
      configTestElement.setProperty(RTESampler.CONFIG_CONNECTION_TIMEOUT,
          rteConfigPanelConfigPanel.getConnectionTimeout());

    }
  }

}
