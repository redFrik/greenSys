GreenScope : SCViewHolder {

	*new {|parent, bounds, server, index= 0|
		^super.new.initGreenScope(parent, bounds, server, index)
	}

	initGreenScope {|parent, bounds, argServer, index|

		var skin= GUI.skins.guiCV;
		var scopeView, userView;
		var server= argServer ? Server.default;

		var bus= Bus(\audio, index, 2, server);
		var synth;
		var startFunc= {|server|
			synth= BusScopeSynth(server);
			synth.play(2048, bus, 1024);  //TODO experiment with these
			scopeView
			.bufnum_(synth.bufferIndex)
			.server_(server)
			.start;
		};

		scopeView= ScopeView(parent, bounds)
		.style_(2)
		.waveColors_(skin.highlight!2)
		.yZoom_(0.5.sqrt);

		userView= UserView(parent, bounds)
		.acceptsMouse_(false)
		.canFocus_(false)
		.drawFunc_({|usr|
			var w= usr.bounds.width*0.5;
			var h= usr.bounds.height*0.5;
			var d= w.min(h);
			Pen.translate(w, h);
			Pen.strokeColor= skin.foreground;
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d*0.5, d*0.5));
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d, d));
			Pen.moveTo(Point(0-d*0.1, 0));
			Pen.lineTo(Point(d*0.1, 0));
			Pen.moveTo(Point(0, 0-d*0.1));
			Pen.lineTo(Point(0, d*0.1));
			Pen.stroke;
		});

		view= View(parent, bounds).layout_(
			StackLayout(userView, scopeView).mode_(\stackAll)
		)
		.background_(skin.background)
		.minSize_(Size(150, 150));

		if(bounds.isNil, {
			view.front;
		});

		ServerTree.add(startFunc, server);
		if(server.serverRunning, {
			startFunc.value(server);
		});

		scopeView.onClose_({
			scopeView.stop;
			bus.free;
			synth.free;
			ServerTree.remove(startFunc, server);
		});
	}
}
