package kr.co.photointerior.kosw.service.beacon;

/**
 * 기압기준 고도 계산 클래스.
 */
public class PresureHeight {

    // Air molar mass             = 0.0289644
    // Gravitational acceleration = 9.80665
    // Gas constant               = 8.31446217576

    private static final double ugR = (-1 * 0.0289644 * 9.80665) / 8.31446217576;
    private static final double normalPresure = 1013.25; // hPa;

    private double presureStart; // Pa
    private double presureFinal; // Pa
    private double altitude;      // m
    private double temperature;   // K

    private void calculateAltitude(boolean useNormalPresure) {
        if (this.temperature == 0) {
            return;
        }
        if (useNormalPresure) {
            this.altitude = Math.log(this.presureFinal / normalPresure) / (ugR / this.temperature);
        }else {
            this.altitude = Math.log(this.presureFinal / this.presureStart) / (ugR / this.temperature);
        }
    }

    public void setPresureStart(double value) {
        this.presureStart = value;
    }

    public double getPresureStart() {
        return this.presureStart;
    }

    public void setPresureFinal(double value) {
        this.presureFinal = value;
    }

    public void setTemperatureKelvin(double value) {
        this.temperature = value;
        calculateAltitude(false);
    }

    public void setTemperatureCelcius(double value) {
        this.temperature = value + 273.15;
        calculateAltitude(false);
    }

    public double getAltitude(boolean use_normal_presure) {
        calculateAltitude(use_normal_presure);
        return this.altitude;
    }
}
