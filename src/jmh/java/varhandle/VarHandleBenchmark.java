package varhandle;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput) // Measures operations per second
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class VarHandleBenchmark {

    private int value = 0;
    private static final VarHandle HANDLE;

    static {
        try {
            HANDLE = MethodHandles.lookup()
                    .findVarHandle(VarHandleBenchmark.class, "value", int.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    @Benchmark
    public int testPlain() {
        // No memory barriers. Maximum speed.
        int current = (int) HANDLE.get(this);
        HANDLE.set(this, current + 1);
        return current;
    }

    @Benchmark
    public int testOpaque() {
        // Bitwise atomic, forbids compiler caching, no CPU fences.
        int current = (int) HANDLE.getOpaque(this);
        HANDLE.setOpaque(this, current + 1);
        return current;
    }

    @Benchmark
    public int testAcquireRelease() {
        // Directional fences. Ensures happens-before ordering.
        int current = (int) HANDLE.getAcquire(this);
        HANDLE.setRelease(this, current + 1);
        return current;
    }

    @Benchmark
    public int testVolatile() {
        // Full global fence / Sequential consistency. Heaviest impact.
        int current = (int) HANDLE.getVolatile(this);
        HANDLE.setVolatile(this, current + 1);
        return current;
    }
}

