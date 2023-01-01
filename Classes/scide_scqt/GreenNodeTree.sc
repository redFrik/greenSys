//TODO window size?

//related: GreenMeter, Server

GreenNodeTree : SCViewHolder {

	*new {|parent, bounds, server, rate= 3|
		^super.new.initGreenNodeTree(parent, bounds, server, rate)
	}

	initGreenNodeTree {|parent, bounds, argServer, rate|

		var skin= GUI.skins.guiCV;
		var canvas, staticText;
		var server= argServer ? Server.default;

		var responder;
		var task;
		var startFunc= {|server|
			var lastMsg;

			responder= OSCFunc({|msg|
				var i= 2, tabs= 0, dumpFunc, str;

				if(msg.size!=lastMsg.size or:{msg!=lastMsg}, {
					str= ("NODE TREE Group"+msg[2]++"\n");

					dumpFunc= {|numChildren|
						var j;
						tabs= tabs+1;
						numChildren.do{
							if(msg[i+1]>=0, {
								i= i+2;  //following is a group
							}, {
								i= i+3+(msg[i+3]*2+1);  //following is a synth
							});
							tabs.do{str= str++"  "};
							str= str++msg[i];  //nodeID

							if(msg[i+1]>=0, {
								str= str+"Group\n";
								if(msg[i+1]>0, {
									dumpFunc.value(msg[i+1]);
								});

							}, {
								str= str+msg[i+2];  //defname

								j= 0;
								msg[i+3].do{
									var v;
									str= str++" ";
									v= msg[i+4+j];
									if(v.isMemberOf(Symbol), {
										str= str++v++": ";
									});
									v= msg[i+5+j];
									if(v.isFloat, {
										v= GUICV.fixDec(v, 2);
									});
									str= str++v;
									j= j+2;
								};
								str= str++"\n";
							});
						};
						tabs= tabs-1;
					};
					dumpFunc.value(msg[3]);
					{staticText.string_(str)}.defer;

					lastMsg= msg;
				});
			}, '/g_queryTree.reply', server.addr);

			task= Routine.run({
				inf.do{
					server.sendMsg("/g_queryTree", 0, 1);
					(1/rate).wait;
				};
			});
		};

		staticText= GUICV.staticText
		.fixedWidth_(1440);  //TODO improve

		canvas= View().background_(skin.background).layout_(
			VLayout([staticText, align: \top]).margins_(skin.margin.asArray)
		);
		view= ScrollView()
		.canFocus_(false)
		.canvas_(canvas)
		.hasBorder_(false);

		if(bounds.isNil, {
			view.front;
			view.resizeTo(640, 480);
		});

		ServerTree.add(startFunc, server);
		if(server.serverRunning, {
			startFunc.value(server);
		});

		view.onClose_({
			task.stop;
			responder.free;
			ServerTree.remove(startFunc, server);
		});
	}
}
