package aurora.bpm.queue;

import aurora.bpm.command.Command;

public interface IQueueListener {
	void onCommand(int queue_id,Command cmd);
	void onException(int queue_id,Throwable thr,Command cmd);
}
