package io.github.jo_bitsch.aoa_control.utils

import android.hardware.usb.UsbAccessory


/**
 * Support class to parse the announced properties of a USB attached Linux device communicating via
 * the AOAv1 and AOAv2 protocol.
 *
 * The corresponding linux tool can be found here: https://github.com/jo-bitsch/aoa-proxy/
 */
data class UsbAccessoryData(
    /**
     * Manufacturer of the device. Maximum 255 bytes.
     *
     * For AOA Proxy Linux hosts, this should always be set to "aoa-proxy", so that we know, that we
     * can parse the other fields accordingly and automatically start the App on USB connection.
     */
    val manufacturer: String = "",
    /**
     * Model type of the device. Maximum 255 bytes.
     *
     * This would be something like
     * "Raspberry Pi 4 Model B".
     *
     * Different hardware model identifiers can be combined via newlines, such as
     * "Raspberry Pi 4 Model B\nSense HAT"
     *
     * This field also clarifies which [ServiceTypes] this device supports. Use it like:
     * "Raspberry Pi 4 Model B\nSense HAT\nServices:ssh,rfb"
     */
    val model: String = "",
    /**
     * Version of the Device. Maximum 255 bytes.
     *
     * We interpret this as software version, such as
     * "Ubuntu 23.04" or "Ubuntu 23.04\nLinux 6.2.0-25-generic"
     */
    val version: String? = null,
    /**
     * Description of the device. Maximum 255 bytes.
     *
     * This string will be displayed, even when this App is not installed yet, together with a link
     * to the [uri].
     * On my tests, a phone would display about the first 50 (fix: verify number) characters,
     * while a tablet displays about the first 60 (fix: verify number).
     *
     * We use the following structure:
     * "Model Name\nIP: 192.168.0.1\n127.0.0.1"
     * as this already allows a user without our App installed some useful information: How can I
     * reach this device on the local network.
     */
    val description: String? = null,
    /**
     * URI pointing to information about the device. Maximum 255 bytes.
     *
     * There will be a link in the Android system UI, even when this App is not installed yet, so
     * this is a great resource for onboarding without change of medium. This could be pointing to
     * an explainer website or the App Store.
     *
     * We currently recommend: "https://github.com/jo-bitsch/aoa-proxy/"
     */
    val uri: String? = null,
    /**
     * Serial of this device. Maximum 255 bytes.
     *
     * We use this to communicate the hostname of the device. Such as:
     * "laptop.local" or
     * "laptop.local\nABC12345"
     *
     * This field needs permission to read from [UsbAccessory.getSerial]. If we don't have
     * permission, we will just return null.
     */
    val serial: String? = null,
) {
    /**
     * The Android Open Accessory might support multiple services. The Android device could be used
     * as a screen as if it was plugged via HDMI by using RFB or RDP protocol. We could simply
     * inject Wi-Fi credentials or connect to SSH and from there on to any number of services, such
     * as Cockpit (https://cockpit-project.org/). A direct connection to a TLS or HTTP socket can
     * also be established.
     *
     *
     * Following the precedent set in https://www.rfc-editor.org/rfc/rfc7983.html#section-5
     * we can select which protocol on the remote (Android Open Accessory) device we want to connect
     * to based on the first byte that we send.
     */
    enum class ServiceTypes {
        /**
         * First bytes on the wire: ""
         * Could be anything. We select " " (Space) as this usually does the least harm, as opposed to
         * carriage return/new line.
         * Alternatively, we could select '\x1B' (Escape)
         *
         *
         *
         * Protocol documentation:
         * maybe terminal emulation like vt100
         * https://sw.kovidgoyal.net/kitty/protocol-extensions/
         * https://iterm2.com/documentation-escape-codes.html
         *
         * On the linux device, this might be connected to a console like:
         * sudo socat unix-listen:/tmp/test.sock exec:/bin/login,pty,echo=0,ctty,setsid
         */
        Console,

        /**
         * First bytes on the wire: "SSH-2"
         *
         * Protocol documentation:
         * https://www.openssh.com/specs.html
         */
        SSH,

        /**
         * First bytes on the wire: "RFB"
         * This is the VNC protocol.
         *
         * Protocol documentation:
         * https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst
         */
        RFB,

        /**
         * First bytes on the wire: "\x03", comes from the TPKT header
         * This is the windows screen sharing/remote desktop protocol
         *
         * Protocol documentation:
         * https://learn.microsoft.com/en-us/troubleshoot/windows-server/remote/understanding-remote-desktop-protocol
         */
        RDP,

        /**
         * First bytes on the wire: "\x16",
         *
         * Protocol documentation:
         * https://datatracker.ietf.org/doc/html/rfc8446
         */
        TLS,

        /**
         * First bytes on the wire: "GET", e.g., WebSocket with connection upgrade
         *
         * Protocol documentation for WebSockets:
         * https://websockets.spec.whatwg.org/
         */
        HTTP,

        /**
         * First bytes on the wire: "\x10", see
         * https://docs.oasis-open.org/mqtt/mqtt/v5.0/cs02/mqtt-v5.0-cs02.html#_CONNECT_Fixed_Header
         *
         * Protocol documentation:
         * https://docs.oasis-open.org/mqtt/mqtt/v5.0/cs02/mqtt-v5.0-cs02.html
         */
        MQTT,

        /**
         * First bytes on the wire: "IMPROV"
         *
         * Protocol documentation:
         * https://www.improv-wifi.com/serial/
         */
        ImprovWifi,

        /**
         * First bytes on the wire: "SSH-2", see [SSH]
         * This connects via SSH and then connects to Cockpit
         *
         * Protocol documentation:
         * https://cockpit-project.org/
         * https://cockpit-project.org/guide/latest/cockpit-ws.8.html
         */
        Cockpit,
    }

    /**
     * Parse the [description] to extract which IP addresses this device might currently have.
     */
    val ipAddresses: List<String>
        get() {
            val ips =
                description?.split("IP: ".toRegex(), 2) ?: return emptyList()

            if (ips.size < 2) return emptyList()

            return ips[1].split('\n').filter {
                it.isNotEmpty()
            }.map { it.split("\t").first() }.distinct()
        }

    /**
     * Parse the [model] to extract which [ServiceTypes] this device supports.
     */
    val services: List<ServiceTypes>
        get() {
            val ret = mutableSetOf<ServiceTypes>()
            for (service in ServiceTypes.entries) {
                if (model.contains(service.name, true))
                    ret.add(service)
            }
            return ret.toList()
        }

    val modelName: String
        get() {
            return model.split('\n').first()
        }

    fun isAOAProxy(): Boolean {
        return manufacturer == "aoa-proxy"
    }

    companion object {

        /**
         * Helper function to help us extract information from an [UsbAccessory].
         *
         * Bonus: this allows us to test easier.
         */
        val UsbAccessory.usbAccessoryData: UsbAccessoryData
            get() {
                return UsbAccessoryData(
                    manufacturer,
                    model,
                    version,
                    description,
                    uri,
                    try {
                        serial
                    } catch (_: SecurityException) {
                        null
                    }
                )
            }

    }
}

