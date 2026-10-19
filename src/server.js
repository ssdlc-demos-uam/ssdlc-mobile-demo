import 'dotenv/config';
import { createApp } from './app.js';

const port = Number(process.env.PORT || 3000);
const app = createApp({ jwtSecret: process.env.JWT_SECRET, allowedOrigin: process.env.ALLOWED_ORIGIN });
app.listen(port, () => console.warn(`API disponible en http://localhost:${port}`));
