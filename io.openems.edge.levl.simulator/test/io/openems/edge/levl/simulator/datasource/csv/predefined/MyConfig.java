package io.openems.edge.levl.simulator.datasource.csv.predefined;

import io.openems.common.test.AbstractComponentConfig;
import io.openems.edge.levl.simulator.CsvFormat;

@SuppressWarnings("all")
public class MyConfig extends AbstractComponentConfig implements Config {

	protected static class Builder {
		private String id;
		private float factor;
		private int timeDelta;
		private Source source;
		private CsvFormat format;

		private Builder() {
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setFactor(float factor) {
			this.factor = factor;
			return this;
		}

		public Builder setTimeDelta(int timeDelta) {
			this.timeDelta = timeDelta;
			return this;
		}

		public Builder setSource(Source source) {
			this.source = source;
			return this;
		}

		public Builder setFormat(CsvFormat format) {
			this.format = format;
			return this;
		}

		public MyConfig build() {
			return new MyConfig(this);
		}
	}

	/**
	 * Create a Config builder.
	 *
	 * @return a {@link Builder}
	 */
	public static Builder create() {
		return new Builder();
	}

	private final Builder builder;

	private MyConfig(Builder builder) {
		super(Config.class, builder.id);
		this.builder = builder;
	}

	@Override
	public float factor() {
		return this.builder.factor;
	}

	@Override
	public Source source() {
		return this.builder.source;
	}

	@Override
	public CsvFormat format() {
		return this.builder.format;
	}
}