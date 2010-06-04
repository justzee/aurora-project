package uncertain.ide.eclipse.editor.textpage;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;

/**
 * 管理Undo操作，用于监听文本域的文本改变事件，生成Undo操作并记录。<br>
 * 
 * @author qujinlong
 */
public class UndoManager
{
  /*
   * 用于存储历史Undo操作，每改变一次文本内容，就将构造一个Undo操作存入OperationHistory中。
   */
  private final IOperationHistory opHistory;

  /*
   * Undo操作上下文，一般用于在OperationHistory中查找当前文本框的Undo操作。
   */
  private IUndoContext undoContext = null;

  /*
   * 所要监听的需要实现Undo操作的文本框。
   */
  private StyledText styledText = null;

  private int undoLevel = 0;

  public UndoManager(int undoLevel)
  {
    opHistory = OperationHistoryFactory.getOperationHistory();

    setMaxUndoLevel(undoLevel);
  }

  public void setMaxUndoLevel(int undoLevel)
  {
    this.undoLevel = Math.max(0, undoLevel);

    if (isConnected())
      opHistory.setLimit(undoContext, this.undoLevel);
  }

  public boolean isConnected()
  {
    return styledText != null;
  }

  /*
   * 将Undo管理器与指定的StyledText文本框相关联。
   */
  public void connect(StyledText styledText)
  {
    if (! isConnected() && styledText != null)
    {
      this.styledText = styledText;

      if (undoContext == null)
        undoContext = new ObjectUndoContext(this);

      opHistory.setLimit(undoContext, undoLevel);
      opHistory.dispose(undoContext, true, true, false);

      addListeners();
    }
  }

  public void disconnect()
  {
    if (isConnected())
    {
      removeListeners();

      styledText = null;

      opHistory.dispose(undoContext, true, true, true);

      undoContext = null;
    }
  }

  private ExtendedModifyListener extendedModifyListener = null;

  private boolean isUndoing = false;

  /*
   * 向Styled中注册监听文本改变的监听器。
   * 
   * 如果文本改变，就构造一个Undo操作压入Undo操作栈中。
   */
  private void addListeners()
  {
    if (styledText != null)
    {
      extendedModifyListener = new ExtendedModifyListener() {
        public void modifyText(ExtendedModifyEvent event)
        {
          if (isUndoing)
            return;

          String newText = styledText.getText().substring(event.start,
              event.start + event.length);

          UndoableOperation operation = new UndoableOperation(undoContext);

          operation.set(event.start, newText, event.replacedText);

          opHistory.add(operation);
        }
      };

      styledText.addExtendedModifyListener(extendedModifyListener);
    }
  }

  private void removeListeners()
  {
    if (styledText != null)
    {
      if (extendedModifyListener != null)
      {
        styledText.removeExtendedModifyListener(extendedModifyListener);

        extendedModifyListener = null;
      }
    }
  }

  public void redo()
  {
    if (isConnected())
    {
      try
      {
        opHistory.redo(undoContext, null, null);
      }
      catch (ExecutionException ex)
      {
      }
    }
  }

  public void undo()
  {
    if (isConnected())
    {
      try
      {
        opHistory.undo(undoContext, null, null);
      }
      catch (ExecutionException ex)
      {
      }
    }
  }

  /*
   * Undo操作用于记录StyledText的文本被改变时的相关数据。
   * 
   * 比如文本框中本来的文本为111222333，如果此时选中222替换为444（用复制粘帖的方法），
   * 
   * 则Undo操作中记录的相关数据为： startIndex = 3; newText = 444; replacedText = 222;
   */
  private class UndoableOperation extends AbstractOperation
  {
    // 记录Undo操作时,被替换文本的开始索引
    protected int startIndex = - 1;

    // 新输入的文本
    protected String newText = null;

    // 被替换掉的文本
    protected String replacedText = null;

    public UndoableOperation(IUndoContext context)
    {
      super("Undo-Redo");

      addContext(context);
    }

    /*
     * 设置Undo操作中要存储的相关数据。
     */
    public void set(int startIndex, String newText, String replacedText)
    {
      this.startIndex = startIndex;

      this.newText = newText;
      this.replacedText = replacedText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      isUndoing = true;
      styledText.replaceTextRange(startIndex, newText.length(), replacedText);
      isUndoing = false;

      return Status.OK_STATUS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      isUndoing = true;
      styledText.replaceTextRange(startIndex, replacedText.length(), newText);
      isUndoing = false;

      return Status.OK_STATUS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
     *      org.eclipse.core.runtime.IAdaptable)
     */
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }
  }
}

