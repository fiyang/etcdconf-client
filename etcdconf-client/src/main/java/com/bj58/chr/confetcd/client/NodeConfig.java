package com.bj58.chr.confetcd.client;

import lombok.Data;
/**
 * 节点信息，回调时使用
 * @author 58
 *
 */
@Data
public class NodeConfig {
	private String nodeName;
	private String ipAddress;
	private NodeWatchStat stat = NodeWatchStat.NOT_WATCH;
	private IWatchCallBack watchCallBack;
	private ISysWatchCallBack sysWatchCallBack;
}
