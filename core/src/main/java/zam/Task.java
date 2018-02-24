package zam;

@FunctionalInterface
public interface Task
{
  void call()
    throws Exception;
}
