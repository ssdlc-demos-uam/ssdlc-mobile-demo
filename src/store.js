/** Almacenamiento en memoria deliberadamente simple para el laboratorio. */
export function createStore() {
  const users = [];
  const tasks = [];
  const purchases = [];
  let nextUserId = 1;
  let nextTaskId = 1;

  return {
    addUser({ email, passwordHash }) {
      const user = { id: String(nextUserId++), email, passwordHash };
      users.push(user);
      return user;
    },
    findUserByEmail(email) { return users.find((user) => user.email === email); },
    addTask({ ownerId, title, description }) {
      const task = { id: String(nextTaskId++), ownerId, title, description, done: false };
      tasks.push(task);
      return task;
    },
    listTasks(ownerId) { return tasks.filter((task) => task.ownerId === ownerId); },
    findTask(id) { return tasks.find((task) => task.id === id); },
    removeTask(task) { tasks.splice(tasks.indexOf(task), 1); },
    addPurchase({ ownerId, productId, priceCents }) {
      const purchase = { id: String(purchases.length + 1), ownerId, productId, priceCents, status: 'SIMULATED' };
      purchases.push(purchase);
      return { id: purchase.id, productId, priceCents, status: purchase.status };
    }
  };
}
