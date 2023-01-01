//TODO override cmd+m somehow

//related: GreenMeter

GreenServerMeter : SCViewHolder {
	var inMeters, outMeters;

	*new {|parent, bounds, server, numIns, numOuts, position, rate= 30|
		^super.new.initGreenServerMeter(parent, bounds, server, numIns, numOuts, position, rate)
	}

	initGreenServerMeter {|parent, bounds, server, numIns, numOuts, position, rate|

		var skin= GUI.skins.guiCV;

		server= server ? Server.default;
		numIns= numIns ? server.options.numInputBusChannels;
		numOuts= numOuts ? server.options.numOutputBusChannels;
		position= position ?? {Point(0, 600)};

		inMeters= GreenMeter(
			target: RootNode(server),
			addAction: \addToHead,
			index: server.options.numOutputBusChannels,
			numChannels: numIns,
			rate: rate
		);

		outMeters= GreenMeter(
			target: RootNode(server),
			addAction: \addToTail,
			index: 0,
			numChannels: numOuts,
			rate: rate
		);

		view= View(parent, bounds).layout_(
			GridLayout.rows(
				[
					GUICV.staticText.string_("Inputs ")
					.maxHeight_(skin.buttonHeight),
					nil,
					GUICV.staticText.string_("Outputs")
					.maxHeight_(skin.buttonHeight)
				],
				[
					inMeters,
					View().background_(skin.foreground).fixedWidth_(1),
					outMeters
				]
			).margins_([skin.margin.x, 0]).spacing_(skin.spacing)
		)
		.background_(skin.background);

		if(bounds.isNil, {
			view.resizeTo(
				skin.margin.x*2+inMeters.bounds.width+1+outMeters.bounds.width,
				(skin.buttonHeight+skin.spacing*3)+skin.sliderHeight
			)
			.moveTo(position.x, position.y)
			.front;
		});
	}

	resetPeaks {
		inMeters.resetPeaks;
		outMeters.resetPeaks;
	}
}
