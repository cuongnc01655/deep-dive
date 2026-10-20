package varhandle;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark) // Shared across all threads to force hardware contention
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class VarHandleMultiThreadBenchmark {

    // The shared counter that all threads will hammer simultaneously
    private int sharedCounter = 0;
    private static final VarHandle HANDLE;

    static {
        try {
            HANDLE = MethodHandles.lookup()
                    .findVarHandle(VarHandleMultiThreadBenchmark.class, "sharedCounter", int.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }

    @Benchmark
    @Threads(4) // Simulates 4 concurrent threads fighting on your M1 cores
    public int testContendedAcquireRelease() {
        int current;
        int newValue;
        do {
            current = (int) HANDLE.getAcquire(this);
            newValue = current + 1;
        } while (!HANDLE.compareAndSet(this, current, newValue)); // Atomic spin-loop
        return newValue;
    }

    @Benchmark
    @Threads(4) // Simulates 4 concurrent threads fighting on your M1 cores
    public int testContendedVolatile() {
        int current;
        int newValue;
        do {
            current = (int) HANDLE.getVolatile(this);
            newValue = current + 1;
        } while (!HANDLE.compareAndSet(this, current, newValue)); // Atomic spin-loop
        return newValue;
    }
}
