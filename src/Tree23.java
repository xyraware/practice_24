import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class Tree23<T extends Comparable<T>> {
	private Node root;//корневой узел
	private int size;//количество узлов в дереве
	private static final int ROOT_IS_BIGGER = 1;
	private static final int ROOT_IS_SMALLER = -1;
	private boolean addition;//флаг, обозначающий корректно ли вставлен элемент( если элемент существует, то

	// пользователь будет предупрежден, а параметр будет false)
	public Tree23() {//конструкторы
		this.root = new Node();
		size = 0;
	}

	public boolean add(T element) {
		size++;
		addition = false;
		if (root == null || root.getLeftElement() == null) {
			if (root == null) {
				root = new Node();
			}//элемент добавляется как корневой если такового не имеется
			root.setLeftElement(element);//элемент добавляется как левый от корня и названачается значение
			addition = true;
		} else {
			Node newRoot = addElementI(root, element);
			if (newRoot != null) {
				root = newRoot;
			}
		}
		if (!addition) {
			size--;
		}
		return addition;
	}

	public boolean addAll(Collection<T> elements) {
		boolean ok = true;
		for (T e : elements) {
			if (!add(e)) ok = false;
		}
		return ok;
	}

	public boolean addAllSafe(Collection<T> elements) {
		int inserted = 0, i = 0;
		for (T e : elements) {
			if (!add(e)) {
				for (T a : elements) {
					if (i >= inserted) return false;
					else remove(a);
				}
			} else inserted++;
		}
		return true;
	}

	private Node addElementI(Node current, T element) {
		Node newParent = null;
		if (!current.isLeaf()) {
			Node sonAscended = null;
			if (current.leftElement.compareTo(element) == 0 || (current.is3Node() && current.rightElement.compareTo(element) == 0)) {
				System.out.println("Элемент уже существует");
			}
			//Новый элемент меньше, чем левый элемент
			else if (current.leftElement.compareTo(element) == ROOT_IS_BIGGER) {
				sonAscended = addElementI(current.left, element);//элемент добавляется как тройной узел(где 2 значения)
				if (sonAscended != null) {
					//новый узел пойдет на левую ветку. Он всегда меньше, чем левый узел от текущего узла
					if (current.is2Node()) {
						current.rightElement = current.leftElement;  // shift the current left element to the right
						current.leftElement = sonAscended.leftElement;
						current.right = current.mid;
						current.mid = sonAscended.mid;
						current.left = sonAscended.left;
					}
					//в этом случае добавлякм новый элемент слева
					else {
						//копируем правое поддерево
						Node rightCopy = new Node(current.rightElement, null, current.mid, current.right);
						//создаем новую структуру
						newParent = new Node(current.leftElement, null, sonAscended, rightCopy);
					}
				}
				//элемент больше, чем левый и меньше чем правый
			} else if (current.is2Node() || (current.is3Node() && current.rightElement.compareTo(element) == ROOT_IS_BIGGER)) {
				sonAscended = addElementI(current.mid, element);
				if (sonAscended != null) { //новый элемент пойдет на левую часть, а левый элемент на правую
					if (current.is2Node()) {
						current.rightElement = sonAscended.leftElement;
						current.right = sonAscended.mid;
						current.mid = sonAscended.left;
					} else { // деление на 3 листья
						Node left = new Node(current.leftElement, null, current.left, sonAscended.left);
						Node mid = new Node(current.rightElement, null, sonAscended.mid, current.right);
						newParent = new Node(sonAscended.leftElement, null, left, mid);
					}
				}
				//вводимый элемент больше чем правый элемент
			} else if (current.is3Node() && current.rightElement.compareTo(element) == ROOT_IS_SMALLER) {
				sonAscended = addElementI(current.right, element);
				if (sonAscended != null) { //правый элемент пойдет наверх
					Node leftCopy = new Node(current.leftElement, null, current.left, current.mid);
					newParent = new Node(current.rightElement, null, leftCopy, sonAscended);
				}
			}
		} else {
			addition = true;
			//элемент уже существует
			if (current.leftElement.compareTo(element) == 0 || (current.is3Node() && current.rightElement.compareTo(element) == 0)) {
				addition = false;
			} else if (current.is2Node()) {
				if (current.leftElement.compareTo(element) == ROOT_IS_BIGGER) {
					current.rightElement = current.leftElement;
					current.leftElement = element;
				} else if (current.leftElement.compareTo(element) == ROOT_IS_SMALLER) current.rightElement = element;
			} else newParent = split(current, element);
		}
		return newParent;
	}

	private Node split(Node current, T element) {
		Node newParent = null;
		//левый элемент больше, поэтому создаем новый элемент слева
		if (current.leftElement.compareTo(element) == ROOT_IS_BIGGER) {
			Node left = new Node(element, null);
			Node right = new Node(current.rightElement, null);
			newParent = new Node(current.leftElement, null, left, right);

		} else if (current.leftElement.compareTo(element) == ROOT_IS_SMALLER) {
			//Новый элемент больше, чем текущий на справа и меньше, чем правый элемент.Поэтому он идет наверх
			if (current.rightElement.compareTo(element) == ROOT_IS_BIGGER) {
				Node left = new Node(current.leftElement, null);
				Node right = new Node(current.rightElement, null);
				newParent = new Node(element, null, left, right);
			} else {
				//Новый элемент больше, поэтому текущий идет наверх
				Node left = new Node(current.leftElement, null);
				Node right = new Node(element, null);
				newParent = new Node(current.rightElement, null, left, right);
			}
		}
		return newParent;
	}

	public void clear() {//очистка дерева
		this.size = 0;
		this.root = null;
	}

	@Override
	public Tree23<T> clone() {//копирование деревья
		Tree23<T> clone = new Tree23<>();
		if (!isEmpty()) cloneI(root, clone);
		return clone;
	}

	private void cloneI(Node current, Tree23<T> clone) {//опирование узлов
		if (current != null) {
			if (current.isLeaf()) {
				clone.add(current.getLeftElement());
				if (current.getRightElement() != null) clone.add(current.getRightElement());
			} else {
				cloneI(current.getLeftSon(), clone);
				clone.add(current.getLeftElement());
				cloneI(current.getMidSon(), clone);
				if (current.getRightElement() != null) {
					if (!current.isLeaf()) {
						clone.add(current.getRightElement());
					}
					cloneI(current.getRightSon(), clone);
				}
			}
		}
	}

	public boolean contains(T element) {//функция, которая показывает существует ли в дереве элемент,
		// который соответствует данному
		return find(element) != null;
	}

	public T find(T element) {//сравнение узла с вводимым ключом
		return findI(root, element);
	}

	private T findI(Node current, T element) {
		T found = null;
		if (current != null) {
			if (current.leftElement != null && current.leftElement.equals(element)) {
				found = current.leftElement;
			} else {
				if (current.rightElement != null && current.rightElement.equals(element)) {
					found = current.rightElement;
				} else {
					if (current.leftElement.compareTo(element) == ROOT_IS_BIGGER) {
						found = findI(current.left, element);
					} else if (current.right == null || current.rightElement.compareTo(element) == ROOT_IS_BIGGER) {
						found = findI(current.mid, element);
					} else if (current.rightElement.compareTo(element) == ROOT_IS_SMALLER) {
						found = findI(current.right, element);
					} else return null;
				}
			}
		}
		return found;
	}

	public long getLevel() {
		Node aux = root;
		int level = 0;
		while (aux != null) {
			aux = root.getLeftSon();
			level++;
		}
		return level;
	}

	public void inOrder() {
		if (!isEmpty()) {
			inOrderI(root);
		} else System.out.println("The tree is empty");
	}

	private void inOrderI(Node current) {
		if(current != null) {

			if(current.isLeaf()) {

				System.out.println(current.getLeftElement().toString());
				if(current.getRightElement() != null) System.out.println(current.getRightElement().toString());
			}
			else {

				inOrderI(current.getLeftSon());
				System.out.println(current.getLeftElement().toString());

				inOrderI(current.getMidSon());

				if(current.getRightElement() != null) {

					if(!current.isLeaf()) System.out.println(current.getRightElement().toString());

					inOrderI(current.getRightSon());
				}
			}
		}
	}

	public boolean isEmpty() {//проверка на пустоту дерева
		if (root == null) return true;
		if (root.getLeftElement() == null) {
			return true;
		}
		return false;
	}

	public boolean remove(T element) {
		boolean deleted;
		this.size--;
		deleted = removeI(root, element); // Immersion
		root.rebalance();
		if (root.getLeftElement() == null) root = null; // We have deleted the last element of the tree
		if (!deleted) this.size++;
		return deleted;
	}

	private boolean removeI(Node current, T element) {
		boolean deleted = true;
		//элемент не найден
		if (current == null) {
			deleted = false;
		} else {
			//ищем элемент для удаления
			if (!current.getLeftElement().equals(element)) {
				if (current.getRightElement() == null || current.getRightElement().compareTo(element) == ROOT_IS_BIGGER) {
					//левый элемент больше чем удаляемый, поэтому спускаемся через левого потомка
					if (current.getLeftElement().compareTo(element) == ROOT_IS_BIGGER) {
						deleted = removeI(current.left, element);
					} else { //если нет, то через среднего потомка
						deleted = removeI(current.mid, element);
					}
				} else {
					//если удаляемый элемент не равен правому, то пройдем через правого потомка
					if (!current.getRightElement().equals(element)) {
						deleted = removeI(current.right, element);
					} else { // если нет, то найдем элемент
						//  равен правому элементу, поэтому просто удалим его
						if (current.isLeaf()) {
							current.setRightElement(null);
						} else { // Мы нашли элемент, но он не лист, поэтому находим минимальный элемент в ветке
							// , удаляем его из текущей позиции и помещаем его на место удаляемого элемент
							T replacement = current.getRightSon().replaceMin();
							current.setRightElement(replacement);
						}
					}
				}
			}
			//левый элеметн равен удаляемому
			else {
				if (current.isLeaf()) {//левый элемент,теперь становится правым, а лвеый удаляется
					if (current.getRightElement() != null) {
						current.setLeftElement(current.getRightElement());
						current.setRightElement(null);
					} else { // Если элемент не справа, необходим ребаланс. Сосздадим пустой элемент
						current.setLeftElement(null);
						return true;
					}
				} else {//Перемещаем максимальный элемент на левую ветку, где мы нашли элемент
					T replacement = current.getLeftSon().replaceMax();
					current.setLeftElement(replacement);
				}
			}
		}
		if (current != null && !current.isBalanced()) {
			current.rebalance();  //Нижнему уровню необходим ребаланс
		} else if (current != null && !current.isLeaf()) {
			boolean balanced = false;
			while (!balanced) {
				if (current.getRightSon() == null) {
					//левый потомок
					if (current.getLeftSon().isLeaf() && !current.getMidSon().isLeaf()) {
						T replacement = current.getMidSon().replaceMin();
						T readdition = current.getLeftElement();
						current.setLeftElement(replacement);
						add(readdition);
						//правй потомок
					} else if (!current.getLeftSon().isLeaf() && current.getMidSon().isLeaf()) {
						if (current.getRightElement() == null) {
							T replacement = current.getLeftSon().replaceMax();
							T readdition = current.getLeftElement();
							current.setLeftElement(replacement);
							add(readdition);
						}
					}
				}
				if (current.getRightSon() != null) {
					if (current.getMidSon().isLeaf() && !current.getRightSon().isLeaf()) {
						current.getRightSon().rebalance();
					}
					if (current.getMidSon().isLeaf() && !current.getRightSon().isLeaf()) {
						T replacement = current.getRightSon().replaceMin();
						T readdition = current.getRightElement();
						current.setRightElement(replacement);
						add(readdition);
					} else balanced = true;
				}
				if (current.isBalanced()) balanced = true;
			}
		}
		return deleted;
	}

	public int size() {

		return size;
	}

	private class Node {
		private Node left;
		private Node mid;
		private Node right;
		private T leftElement;
		private T rightElement;

		private Node() {

			left = null;
			mid = null;
			right = null;
			leftElement = null;
			rightElement = null;
		}

		private Node(T leftElement, T rightElement) {

			this.leftElement = leftElement;
			this.rightElement = rightElement;
			left = null;
			mid = null;
			right = null;
		}

		private Node(T leftElement, T rightElement, Node left, Node mid) {

			this.leftElement = leftElement;
			this.rightElement = rightElement;
			this.left = left;
			this.mid = mid;
		}

		private T getLeftElement() {

			return leftElement;
		}

		private T getRightElement() {

			return rightElement;
		}

		private void setLeftElement(T element) {

			this.leftElement = element;
		}
		private void setRightElement(T element) {

			this.rightElement = element;
		}

		private void setLeftSon(Node son) {

			this.left = son;
		}

		private Node getLeftSon() {

			return left;
		}

		private void setMidSon(Node son) {

			this.mid = son;
		}

		private Node getMidSon() {

			return mid;
		}

		private void setRightSon(Node son) {

			this.right = son;
		}

		private Node getRightSon() {

			return right;
		}

		private boolean isLeaf() {

			return left == null && mid == null && right == null;
		}

		private boolean is2Node() {

			return rightElement == null; // also, right node is null but this will be always true if rightElement == null
		}

		private boolean is3Node() {

			return rightElement != null; // also, right node is not null but this will be always true if rightElement <> null
		}

		private boolean isBalanced() {
			boolean balanced = false;
			if (isLeaf()) {
				balanced = true;
			} else if (left.getLeftElement() != null && mid.getLeftElement() != null) {
				if (rightElement != null) { // 3 узел
					if (right.getLeftElement() != null) {
						balanced = true;
					}
				} else {  // 2 узел
					balanced = true;
				}
			}
			return balanced;
		}

		private T replaceMax() {
			T max = null;
			if (!isLeaf()) {
				if (getRightElement() != null) {
					max = right.replaceMax();
				} else max = mid.replaceMax();
			} else {
				if (getRightElement() != null) {
					max = getRightElement();
					setRightElement(null);
				} else {
					max = getLeftElement();
					setLeftElement(null);
				}
			}
			if (!isBalanced()) {
				rebalance();
			}
			return max;
		}

		private T replaceMin() {
			T min = null;
			if (!isLeaf()) {
				min = left.replaceMin();

			} else {
				min = leftElement;
				leftElement = null;
				if (rightElement != null) {
					leftElement = rightElement;
					rightElement = null;
				}
			}

			if (!isBalanced()) {
				rebalance();
			}
			return min;
		}

		private void rebalance() {
			while (!isBalanced()) {
				if (getLeftSon().getLeftElement() == null) {
					getLeftSon().setLeftElement(getLeftElement());
					setLeftElement(getMidSon().getLeftElement());
					if (getMidSon().getRightElement() != null) {
						getMidSon().setLeftElement(getMidSon().getRightElement());
						getMidSon().setRightElement(null);
					} else {
						getMidSon().setLeftElement(null);
					}
				} else if (getMidSon().getLeftElement() == null) {
					if (getRightElement() == null) {
						if (getLeftSon().getLeftElement() != null && getLeftSon().getRightElement() == null && getMidSon().getLeftElement() == null) {
							setRightElement(getLeftElement());
							setLeftElement(getLeftSon().getLeftElement());
							setLeftSon(null);
							setMidSon(null);
							setRightSon(null);
						} else {
							getMidSon().setLeftElement(getLeftElement());
							if (getLeftSon().getRightElement() == null) {
								setLeftElement(getLeftSon().getLeftElement());
								getLeftSon().setLeftElement(null);

							} else {
								setLeftElement(getLeftSon().getRightElement());
								getLeftSon().setRightElement(null);
							}
							if (getLeftSon().getLeftElement() == null && getMidSon().getLeftElement() == null) {
								setLeftSon(null);
								setMidSon(null);
								setRightSon(null);
							}
						}
					} else {
						getMidSon().setLeftElement(getRightElement());
						setRightElement(getRightSon().getLeftElement());
						if (getRightSon().getRightElement() != null) {
							getRightSon().setLeftElement(getRightSon().getRightElement());
							getRightSon().setRightElement(null);
						} else {
							getRightSon().setLeftElement(null);
						}
					}
				} else if (getRightElement() != null && getRightSon().getLeftElement() == null) {
					if (getMidSon().getRightElement() != null) {
						getRightSon().setLeftElement(getRightElement());
						setRightElement(getMidSon().getRightElement());
						getMidSon().setRightElement(null);
					} else {
						getMidSon().setRightElement(getRightElement());
						setRightElement(null);
					}
				}
			}
		}
	}
}
