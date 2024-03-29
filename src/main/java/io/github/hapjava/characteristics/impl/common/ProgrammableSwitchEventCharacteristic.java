package io.github.hapjava.characteristics.impl.common;

import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import io.github.hapjava.characteristics.impl.base.EnumCharacteristic;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This characteristic describes an event generated by a programmable switch. For BLE accessories,
 * read request should return the last event. For IP accessory, read request must return "null". The
 * changes are reported via events
 *
 * <p>See {@link ProgrammableSwitchEnum} for possible values.
 */
public class ProgrammableSwitchEventCharacteristic
    extends EnumCharacteristic<ProgrammableSwitchEnum> {

  public ProgrammableSwitchEventCharacteristic(
      Supplier<CompletableFuture<ProgrammableSwitchEnum>> getter,
      Consumer<HomekitCharacteristicChangeCallback> subscriber,
      Runnable unsubscriber) {
    this(ProgrammableSwitchEnum.values(), getter, subscriber, unsubscriber);
  }

  public ProgrammableSwitchEventCharacteristic(
      ProgrammableSwitchEnum[] validValues,
      Supplier<CompletableFuture<ProgrammableSwitchEnum>> getter,
      Consumer<HomekitCharacteristicChangeCallback> subscriber,
      Runnable unsubscriber) {
    super(
        "00000073-0000-1000-8000-0026BB765291",
        "Switch Event",
        validValues,
        Optional.of(getter),
        Optional.empty(),
        Optional.of(subscriber),
        Optional.of(unsubscriber));
  }
}
